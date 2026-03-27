package ru.knockout.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class KnockoutManager {

    private static Title.Times titleTimes(int fadeInTicks, int stayTicks, int fadeOutTicks) {
        return Title.Times.times(
                Duration.ofMillis(fadeInTicks * 50L),
                Duration.ofMillis(stayTicks * 50L),
                Duration.ofMillis(fadeOutTicks * 50L));
    }

    private final JavaPlugin plugin;
    private KnockoutConfig config;
    private final Map<UUID, KnockoutState> byVictim = new ConcurrentHashMap<>();
    /** After a successful revive only: next knockout blocked until this time (epoch ms). Cleared on player death. */
    private final Map<UUID, Long> knockoutRepeatLockedUntil = new ConcurrentHashMap<>();

    public KnockoutManager(JavaPlugin plugin, KnockoutConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void updateConfig(KnockoutConfig config) {
        this.config = config;
    }

    public boolean isKnockedOut(Player player) {
        return byVictim.containsKey(player.getUniqueId());
    }

    public boolean allowEnvironmentalDamageWhileKnocked() {
        return config.allowEnvironmentalDamageWhileKnocked;
    }

    public boolean allowMobKnockout() {
        return config.allowMobKnockout;
    }

    public boolean allowFinisherHit() {
        return config.allowFinisherHit;
    }

    public boolean isKnockoutRepeatCooldownActive(Player victim) {
        UUID id = victim.getUniqueId();
        Long until = knockoutRepeatLockedUntil.get(id);
        if (until == null) {
            return false;
        }
        if (System.currentTimeMillis() >= until) {
            knockoutRepeatLockedUntil.remove(id);
            return false;
        }
        return true;
    }

    public boolean shouldOfferKnockoutForEvent(EntityDamageEvent event, Player victim) {
        if (config.anyLethalTriggersKnockout) {
            return true;
        }
        if (event instanceof EntityDamageByEntityEvent byEntity) {
            Entity source = resolveDamageSource(byEntity.getDamager());
            return source != null && canCauseKnockout(source, victim);
        }
        return false;
    }

    public void startKnockout(Player victim) {
        if (isKnockedOut(victim)) {
            return;
        }
        KnockoutState state = new KnockoutState();
        byVictim.put(victim.getUniqueId(), state);

        victim.leaveVehicle();
        applyCrawlPose(victim);
        applyKnockoutEffects(victim);

        victim.showTitle(
                Title.title(
                        config.knockoutTitle,
                        config.knockoutSubtitle,
                        titleTimes(10, 70, 20)));

        final UUID victimId = victim.getUniqueId();
        state.timeoutTask =
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!byVictim.containsKey(victimId)) {
                            return;
                        }
                        Player p = Bukkit.getPlayer(victimId);
                        if (p != null && p.isOnline() && p.getHealth() > 0) {
                            p.setHealth(0);
                        }
                    }
                }.runTaskLater(plugin, config.knockoutTicks);
    }

    public void dieCommand(Player victim) {
        if (!isKnockedOut(victim)) {
            return;
        }
        victim.setHealth(0);
    }

    public boolean recoveryCommand(Player victim) {
        KnockoutState state = byVictim.get(victim.getUniqueId());
        if (state == null) {
            return false;
        }
        revive(victim, state);
        return true;
    }

    public void removeAndForget(Player victim) {
        KnockoutState state = byVictim.remove(victim.getUniqueId());
        if (state != null && state.timeoutTask != null) {
            state.timeoutTask.cancel();
        }
    }

    /** Any death removes repeat-knockout lock so the player can be knocked out again after respawn. */
    public void clearRepeatKnockoutCooldown(Player victim) {
        knockoutRepeatLockedUntil.remove(victim.getUniqueId());
    }

    public void onQuit(Player victim) {
        if (!isKnockedOut(victim)) {
            return;
        }
        removeAndForget(victim);
        clearCrawlPose(victim);
        victim.removePotionEffect(PotionEffectType.SLOWNESS);
        victim.removePotionEffect(PotionEffectType.BLINDNESS);
        victim.setHealth(0);
    }

    public void tick() {
        List<Map.Entry<UUID, KnockoutState>> snapshot = new ArrayList<>(byVictim.entrySet());
        for (Map.Entry<UUID, KnockoutState> entry : snapshot) {
            Player victim = Bukkit.getPlayer(entry.getKey());
            if (victim == null || !victim.isOnline()) {
                continue;
            }
            tickVictim(victim, entry.getValue());
        }
    }

    private void tickVictim(Player victim, KnockoutState state) {
        applyCrawlPose(victim);
        applyKnockoutEffects(victim);
        trySelfRevive(victim, state);
        if (!isKnockedOut(victim)) {
            return;
        }

        Player rescuer = findRescuer(victim);
        if (rescuer == null) {
            if (state.rescueProgress > 0) {
                state.resetRescue();
                victim.showTitle(
                        Title.title(
                                config.rescueResetTitle,
                                config.rescueResetSubtitle,
                                titleTimes(5, 50, 15)));
            }
            return;
        }

        if (state.activeRescuer == null || !state.activeRescuer.equals(rescuer.getUniqueId())) {
            state.startNewRescue(rescuer.getUniqueId());
        }

        state.rescueProgress++;
        int percent = Math.min(100, state.rescueProgress * 100 / config.rescueTicks);

        if (state.rescueProgress == 1 || state.rescueProgress % 20 == 0) {
            victim.showTitle(
                    Title.title(
                            config.formatProgressTitle(percent),
                            config.rescueProgressSubtitle,
                            titleTimes(0, 35, 10)));
        }

        rescuer.sendActionBar(config.formatRescuerActionbar(percent));

        if (config.rescueParticlesEnabled && state.rescueProgress % config.rescueParticleEveryTicks == 0) {
            victim.getWorld()
                    .spawnParticle(
                            config.rescueParticle,
                            victim.getLocation().add(0, config.rescueParticleYAdd, 0),
                            config.rescueParticleCount,
                            config.rescueParticleOffsetX,
                            config.rescueParticleOffsetY,
                            config.rescueParticleOffsetZ,
                            0);
        }

        if (state.rescueProgress >= config.rescueTicks) {
            revive(victim, state);
        }
    }

    private void revive(Player victim, KnockoutState state) {
        if (state.timeoutTask != null) {
            state.timeoutTask.cancel();
        }
        byVictim.remove(victim.getUniqueId());

        clearCrawlPose(victim);
        victim.removePotionEffect(PotionEffectType.SLOWNESS);
        victim.removePotionEffect(PotionEffectType.BLINDNESS);

        double max =
                Objects.requireNonNull(victim.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                        .getValue();
        double minHealth = config.reviveMinimumHearts * 2.0;
        double targetHealth;
        if ("fixed".equalsIgnoreCase(config.reviveHealthMode)) {
            targetHealth = config.reviveHealthValue;
        } else {
            targetHealth = max * config.reviveHealthValue;
        }
        victim.setHealth(Math.min(max, Math.max(minHealth, targetHealth)));

        victim.showTitle(
                Title.title(
                        config.revivedTitle,
                        Component.empty(),
                        titleTimes(10, 40, 20)));
        if (config.reviveSoundEnabled) {
            victim.playSound(
                    victim.getLocation(),
                    config.reviveSound,
                    config.reviveSoundVolume,
                    config.reviveSoundPitch);
        }
        if (config.postReviveInvulnerabilityTicks > 0) {
            victim.setNoDamageTicks(Math.max(victim.getNoDamageTicks(), config.postReviveInvulnerabilityTicks));
        }
        registerKnockoutRepeatCooldown(victim);
    }

    private void registerKnockoutRepeatCooldown(Player victim) {
        if (config.repeatKnockoutCooldownMs <= 0) {
            return;
        }
        knockoutRepeatLockedUntil.put(
                victim.getUniqueId(), System.currentTimeMillis() + config.repeatKnockoutCooldownMs);
    }

    /** Имитация ползания: плавание + присед на земле (визуально близко к ползанию). */
    private void applyCrawlPose(Player victim) {
        if (!config.crawlEnabled) {
            victim.setSneaking(true);
            victim.setSwimming(true);
            victim.setPose(Pose.SWIMMING);
            return;
        }
        victim.setSneaking(true);
        if (config.crawlPose == Pose.SWIMMING) {
            victim.setSwimming(true);
        } else {
            victim.setSwimming(false);
        }
        victim.setPose(config.crawlPose);
    }

    private void clearCrawlPose(Player victim) {
        victim.setSwimming(false);
        victim.setSneaking(false);
        victim.setPose(Pose.STANDING);
    }

    private void applyKnockoutEffects(Player victim) {
        victim.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.SLOWNESS,
                        config.slownessRefreshDurationTicks,
                        config.effectiveKnockoutSlownessAmplifier(),
                        false,
                        true,
                        true));
        victim.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.BLINDNESS,
                        config.blindnessRefreshDurationTicks,
                        config.blindnessAmplifier,
                        false,
                        true,
                        true));
    }

    private Player findRescuer(Player victim) {
        Location vl = victim.getLocation();
        Player best = null;
        double bestDistSq = Double.MAX_VALUE;

        for (Player other : victim.getWorld().getPlayers()) {
            if (other.equals(victim)) {
                continue;
            }
            if (!other.isSneaking()) {
                continue;
            }
            if (other.getGameMode() == org.bukkit.GameMode.SPECTATOR) {
                continue;
            }
            if (config.teamOnlyRescue && !isSameTeam(victim, other)) {
                continue;
            }

            Location ol = other.getLocation();
            double dx = ol.getX() - vl.getX();
            double dz = ol.getZ() - vl.getZ();
            double h = dx * dx + dz * dz;
            if (h > config.horizontalRescueRadius * config.horizontalRescueRadius) {
                continue;
            }
            if (ol.getY() < vl.getY() + config.rescuerMinYOffset) {
                continue;
            }

            if (h < bestDistSq) {
                bestDistSq = h;
                best = other;
            }
        }
        return best;
    }

    private void trySelfRevive(Player victim, KnockoutState state) {
        if (!config.selfReviveEnabled || state.selfReviveChecked) {
            return;
        }
        state.selfReviveChecked = true;
        SelfReviveCandidate candidate = findSelfRevivePotion(victim);
        if (candidate == null) {
            return;
        }
        double chance = chanceByPotionType(candidate.potionType);
        if (ThreadLocalRandom.current().nextDouble() > chance) {
            return;
        }
        if (config.selfReviveConsumePotion) {
            consumeOneItem(victim, candidate.slot);
        }
        victim.sendMessage(config.selfReviveSuccessMessage);
        revive(victim, state);
    }

    private SelfReviveCandidate findSelfRevivePotion(Player victim) {
        ItemStack[] contents = victim.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType().isAir() || !(item.getItemMeta() instanceof PotionMeta meta)) {
                continue;
            }
            PotionType type = meta.getBasePotionType();
            for (PotionType allowed : config.selfRevivePotionTypes) {
                if (type == allowed) {
                    return new SelfReviveCandidate(i, type);
                }
            }
        }
        return null;
    }

    private double chanceByPotionType(PotionType potionType) {
        if (potionType == PotionType.HEALING) {
            return config.selfReviveHealingChance;
        }
        if (potionType == PotionType.REGENERATION) {
            return config.selfReviveRegenerationChance;
        }
        return config.selfReviveChance;
    }

    private static void consumeOneItem(Player victim, int slot) {
        ItemStack item = victim.getInventory().getItem(slot);
        if (item == null) {
            return;
        }
        int amount = item.getAmount();
        if (amount <= 1) {
            victim.getInventory().setItem(slot, null);
        } else {
            item.setAmount(amount - 1);
            victim.getInventory().setItem(slot, item);
        }
    }

    private static boolean isSameTeam(Player victim, Player other) {
        if (victim.getScoreboard() == null) {
            return false;
        }
        var team = victim.getScoreboard().getEntryTeam(victim.getName());
        if (team == null) {
            return false;
        }
        return team.hasEntry(other.getName());
    }

    public boolean canCauseKnockout(Entity damager, Player victim) {
        if (damager instanceof Player playerDamager) {
            return !playerDamager.equals(victim);
        }
        return config.allowMobKnockout && damager instanceof LivingEntity;
    }

    private static Entity resolveDamageSource(Entity damager) {
        if (damager instanceof Player p) {
            return p;
        }
        if (damager instanceof Projectile proj) {
            ProjectileSource src = proj.getShooter();
            if (src instanceof Player p) {
                return p;
            }
            if (src instanceof Entity e) {
                return e;
            }
        }
        return null;
    }

    static final class KnockoutState {
        BukkitTask timeoutTask;
        private UUID activeRescuer;
        private int rescueProgress;
        private boolean selfReviveChecked;

        KnockoutState() {}

        void resetRescue() {
            activeRescuer = null;
            rescueProgress = 0;
        }

        void startNewRescue(UUID rescuer) {
            activeRescuer = rescuer;
            rescueProgress = 0;
        }
    }

    private static final class SelfReviveCandidate {
        private final int slot;
        private final PotionType potionType;

        private SelfReviveCandidate(int slot, PotionType potionType) {
            this.slot = slot;
            this.potionType = potionType;
        }
    }
}
