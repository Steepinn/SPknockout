package ru.knockout.plugin;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;

public final class KnockoutListener implements Listener {

    private final KnockoutManager manager;

    public KnockoutListener(KnockoutManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLethalDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }
        if (manager.isKnockedOut(victim)) {
            return;
        }
        double damage = event.getFinalDamage();
        if (victim.getHealth() - damage > 0) {
            return;
        }
        if (manager.isKnockoutRepeatCooldownActive(victim)) {
            return;
        }
        if (!manager.shouldOfferKnockoutForEvent(event, victim)) {
            return;
        }
        event.setCancelled(true);
        victim.setHealth(1.0);
        manager.startKnockout(victim);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }
        if (!manager.isKnockedOut(victim)) {
            return;
        }
        if (manager.allowEnvironmentalDamageWhileKnocked()) {
            return;
        }
        if (event instanceof EntityDamageByEntityEvent byEntity) {
            Player damager = resolvePlayerDamager(byEntity.getDamager());
            if (damager != null) {
                if (manager.allowFinisherHit()) {
                    event.setCancelled(true);
                    victim.setHealth(0);
                }
                return;
            }
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        manager.removeAndForget(player);
        manager.clearRepeatKnockoutCooldown(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        manager.onQuit(event.getPlayer());
    }

    private static Player resolvePlayerDamager(Entity damager) {
        Entity source = resolveDamageSource(damager);
        if (source instanceof Player p) {
            return p;
        }
        return null;
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
}
