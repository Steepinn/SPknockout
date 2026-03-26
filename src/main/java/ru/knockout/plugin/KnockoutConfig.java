package ru.knockout.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Pose;
import org.bukkit.potion.PotionType;

public final class KnockoutConfig {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    final int knockoutTicks;
    final int rescueTicks;
    final double horizontalRescueRadius;
    final double rescuerMinYOffset;
    final boolean allowEnvironmentalDamageWhileKnocked;
    final boolean anyLethalTriggersKnockout;
    final long repeatKnockoutCooldownMs;
    final boolean allowMobKnockout;
    final boolean allowFinisherHit;
    final boolean teamOnlyRescue;
    final int postReviveInvulnerabilityTicks;
    final boolean selfReviveEnabled;
    final double selfReviveChance;
    final double selfReviveHealingChance;
    final double selfReviveRegenerationChance;
    final boolean selfReviveConsumePotion;
    final String reviveHealthMode;
    final double reviveHealthValue;
    final double reviveMinimumHearts;
    final int slownessAmplifier;
    final int slownessRefreshDurationTicks;
    final int blindnessAmplifier;
    final int blindnessRefreshDurationTicks;
    final boolean crawlEnabled;
    final int crawlSlownessAmplifier;
    final Pose crawlPose;
    final Component onlyPlayersMessage;
    final Component notKnockedMessage;
    final Component noPermissionMessage;
    final Component reloadSuccessMessage;
    final Component selfReviveSuccessMessage;
    final Component knockoutTitle;
    final Component knockoutSubtitle;
    final Component rescueResetTitle;
    final Component rescueResetSubtitle;
    final String rescueProgressTitleTemplate;
    final Component rescueProgressSubtitle;
    final String rescuerActionbarTemplate;
    final Component revivedTitle;
    final boolean rescueParticlesEnabled;
    final Particle rescueParticle;
    final int rescueParticleEveryTicks;
    final int rescueParticleCount;
    final double rescueParticleOffsetX;
    final double rescueParticleOffsetY;
    final double rescueParticleOffsetZ;
    final double rescueParticleYAdd;
    final boolean reviveSoundEnabled;
    final Sound reviveSound;
    final float reviveSoundVolume;
    final float reviveSoundPitch;
    final String reloadPermission;
    final PotionType[] selfRevivePotionTypes;
    final String locale;

    private KnockoutConfig(
            int knockoutTicks,
            int rescueTicks,
            double horizontalRescueRadius,
            double rescuerMinYOffset,
            boolean allowEnvironmentalDamageWhileKnocked,
            boolean anyLethalTriggersKnockout,
            long repeatKnockoutCooldownMs,
            boolean allowMobKnockout,
            boolean allowFinisherHit,
            boolean teamOnlyRescue,
            int postReviveInvulnerabilityTicks,
            boolean selfReviveEnabled,
            double selfReviveChance,
            double selfReviveHealingChance,
            double selfReviveRegenerationChance,
            boolean selfReviveConsumePotion,
            String reviveHealthMode,
            double reviveHealthValue,
            double reviveMinimumHearts,
            int slownessAmplifier,
            int slownessRefreshDurationTicks,
            int blindnessAmplifier,
            int blindnessRefreshDurationTicks,
            boolean crawlEnabled,
            int crawlSlownessAmplifier,
            Pose crawlPose,
            Component onlyPlayersMessage,
            Component notKnockedMessage,
            Component noPermissionMessage,
            Component reloadSuccessMessage,
            Component selfReviveSuccessMessage,
            Component knockoutTitle,
            Component knockoutSubtitle,
            Component rescueResetTitle,
            Component rescueResetSubtitle,
            String rescueProgressTitleTemplate,
            Component rescueProgressSubtitle,
            String rescuerActionbarTemplate,
            Component revivedTitle,
            boolean rescueParticlesEnabled,
            Particle rescueParticle,
            int rescueParticleEveryTicks,
            int rescueParticleCount,
            double rescueParticleOffsetX,
            double rescueParticleOffsetY,
            double rescueParticleOffsetZ,
            double rescueParticleYAdd,
            boolean reviveSoundEnabled,
            Sound reviveSound,
            float reviveSoundVolume,
            float reviveSoundPitch,
            String reloadPermission,
            PotionType[] selfRevivePotionTypes,
            String locale) {
        this.knockoutTicks = knockoutTicks;
        this.rescueTicks = rescueTicks;
        this.horizontalRescueRadius = horizontalRescueRadius;
        this.rescuerMinYOffset = rescuerMinYOffset;
        this.allowEnvironmentalDamageWhileKnocked = allowEnvironmentalDamageWhileKnocked;
        this.anyLethalTriggersKnockout = anyLethalTriggersKnockout;
        this.repeatKnockoutCooldownMs = repeatKnockoutCooldownMs;
        this.allowMobKnockout = allowMobKnockout;
        this.allowFinisherHit = allowFinisherHit;
        this.teamOnlyRescue = teamOnlyRescue;
        this.postReviveInvulnerabilityTicks = postReviveInvulnerabilityTicks;
        this.selfReviveEnabled = selfReviveEnabled;
        this.selfReviveChance = selfReviveChance;
        this.selfReviveHealingChance = selfReviveHealingChance;
        this.selfReviveRegenerationChance = selfReviveRegenerationChance;
        this.selfReviveConsumePotion = selfReviveConsumePotion;
        this.reviveHealthMode = reviveHealthMode;
        this.reviveHealthValue = reviveHealthValue;
        this.reviveMinimumHearts = reviveMinimumHearts;
        this.slownessAmplifier = slownessAmplifier;
        this.slownessRefreshDurationTicks = slownessRefreshDurationTicks;
        this.blindnessAmplifier = blindnessAmplifier;
        this.blindnessRefreshDurationTicks = blindnessRefreshDurationTicks;
        this.crawlEnabled = crawlEnabled;
        this.crawlSlownessAmplifier = crawlSlownessAmplifier;
        this.crawlPose = crawlPose;
        this.onlyPlayersMessage = onlyPlayersMessage;
        this.notKnockedMessage = notKnockedMessage;
        this.noPermissionMessage = noPermissionMessage;
        this.reloadSuccessMessage = reloadSuccessMessage;
        this.selfReviveSuccessMessage = selfReviveSuccessMessage;
        this.knockoutTitle = knockoutTitle;
        this.knockoutSubtitle = knockoutSubtitle;
        this.rescueResetTitle = rescueResetTitle;
        this.rescueResetSubtitle = rescueResetSubtitle;
        this.rescueProgressTitleTemplate = rescueProgressTitleTemplate;
        this.rescueProgressSubtitle = rescueProgressSubtitle;
        this.rescuerActionbarTemplate = rescuerActionbarTemplate;
        this.revivedTitle = revivedTitle;
        this.rescueParticlesEnabled = rescueParticlesEnabled;
        this.rescueParticle = rescueParticle;
        this.rescueParticleEveryTicks = rescueParticleEveryTicks;
        this.rescueParticleCount = rescueParticleCount;
        this.rescueParticleOffsetX = rescueParticleOffsetX;
        this.rescueParticleOffsetY = rescueParticleOffsetY;
        this.rescueParticleOffsetZ = rescueParticleOffsetZ;
        this.rescueParticleYAdd = rescueParticleYAdd;
        this.reviveSoundEnabled = reviveSoundEnabled;
        this.reviveSound = reviveSound;
        this.reviveSoundVolume = reviveSoundVolume;
        this.reviveSoundPitch = reviveSoundPitch;
        this.reloadPermission = reloadPermission;
        this.selfRevivePotionTypes = selfRevivePotionTypes;
        this.locale = locale;
    }

    static KnockoutConfig from(FileConfiguration cfg) {
        int knockoutTicks = Math.max(1, cfg.getInt("knockout.duration-seconds", 60) * 20);
        int rescueTicks = Math.max(1, cfg.getInt("knockout.rescue-seconds", 10) * 20);
        double horizontalRescueRadius = Math.max(0.1, cfg.getDouble("knockout.horizontal-rescue-radius", 1.75));
        double rescuerMinYOffset = cfg.getDouble("knockout.rescuer-min-y-offset", -0.35);
        boolean allowEnv = cfg.getBoolean("knockout.allow-environmental-damage-while-knocked", false);
        boolean anyLethalTriggersKnockout = cfg.getBoolean("knockout.any-lethal-triggers-knockout", true);
        long repeatKnockoutCooldownMs =
                Math.max(0L, cfg.getLong("knockout.repeat-knockout-cooldown-seconds", 60L) * 1000L);
        boolean allowMobKnockout = cfg.getBoolean("knockout.allow-mob-knockout", false);
        boolean allowFinisherHit = cfg.getBoolean("knockout.allow-finisher-hit", false);
        boolean teamOnlyRescue = cfg.getBoolean("knockout.team-only-rescue", false);
        int postReviveInvulnerabilityTicks =
                Math.max(0, cfg.getInt("knockout.post-revive-invulnerability-seconds", 0) * 20);
        boolean selfReviveEnabled = cfg.getBoolean("knockout.self-revive.enabled", true);
        double selfReviveChance = Math.max(0.0, Math.min(1.0, cfg.getDouble("knockout.self-revive.chance", 0.25)));
        double selfReviveHealingChance = Math.max(
                0.0,
                Math.min(
                        1.0,
                        cfg.getDouble("knockout.self-revive.chances.healing", selfReviveChance)));
        double selfReviveRegenerationChance = Math.max(
                0.0,
                Math.min(
                        1.0,
                        cfg.getDouble("knockout.self-revive.chances.regeneration", selfReviveChance)));
        boolean selfReviveConsumePotion = cfg.getBoolean("knockout.self-revive.consume-potion", true);
        String locale = cfg.getString("language.locale", "en").toLowerCase();
        if (!locale.equals("ru")) {
            locale = "en";
        }

        String reviveHealthMode = cfg.getString("knockout.revive-health.mode", "percent-max");
        double reviveHealthValue = cfg.getDouble("knockout.revive-health.value", 0.45);
        double reviveMinimumHearts = Math.max(0.5, cfg.getDouble("knockout.revive-health.minimum-hearts", 3.0));

        int slownessAmplifier = Math.max(0, cfg.getInt("knockout.effects.slowness.amplifier", 6));
        int slownessRefreshDurationTicks = Math.max(1, cfg.getInt("knockout.effects.slowness.refresh-duration-ticks", 100));
        int blindnessAmplifier = Math.max(0, cfg.getInt("knockout.effects.blindness.amplifier", 0));
        int blindnessRefreshDurationTicks = Math.max(1, cfg.getInt("knockout.effects.blindness.refresh-duration-ticks", 100));
        boolean crawlEnabled = cfg.getBoolean("knockout.crawl.enabled", true);
        int crawlSlownessAmplifier = Math.max(0, cfg.getInt("knockout.crawl.slowness-amplifier", 3));
        Pose crawlPose = parsePose(cfg.getString("knockout.crawl.pose", "CRAWLING"));

        Component onlyPlayers = deserialize(message(cfg, locale, "only-players"));
        Component notKnocked = deserialize(message(cfg, locale, "not-knocked"));
        Component noPermission = deserialize(message(cfg, locale, "no-permission"));
        Component reloadSuccess = deserialize(message(cfg, locale, "reload-success"));
        Component selfReviveSuccess = deserialize(message(cfg, locale, "self-revive-success"));
        Component knockoutTitle = deserialize(message(cfg, locale, "knockout-title"));
        Component knockoutSubtitle = deserialize(message(cfg, locale, "knockout-subtitle"));
        Component rescueResetTitle = deserialize(message(cfg, locale, "rescue-reset-title"));
        Component rescueResetSubtitle = deserialize(message(cfg, locale, "rescue-reset-subtitle"));
        String rescueProgressTitleTemplate = message(cfg, locale, "rescue-progress-title");
        Component rescueProgressSubtitle = deserialize(message(cfg, locale, "rescue-progress-subtitle"));
        String rescuerActionbarTemplate = message(cfg, locale, "rescuer-actionbar");
        Component revivedTitle = deserialize(message(cfg, locale, "revived-title"));

        boolean rescueParticlesEnabled = cfg.getBoolean("visuals.rescue-particles.enabled", true);
        Particle rescueParticle = parseParticle(cfg.getString("visuals.rescue-particles.particle", "HEART"));
        int rescueParticleEveryTicks = Math.max(1, cfg.getInt("visuals.rescue-particles.every-ticks", 5));
        int rescueParticleCount = Math.max(1, cfg.getInt("visuals.rescue-particles.count", 2));
        double rescueParticleOffsetX = Math.max(0, cfg.getDouble("visuals.rescue-particles.offset-x", 0.25));
        double rescueParticleOffsetY = Math.max(0, cfg.getDouble("visuals.rescue-particles.offset-y", 0.12));
        double rescueParticleOffsetZ = Math.max(0, cfg.getDouble("visuals.rescue-particles.offset-z", 0.25));
        double rescueParticleYAdd = cfg.getDouble("visuals.rescue-particles.y-add", 0.35);

        boolean reviveSoundEnabled = cfg.getBoolean("sounds.revive.enabled", true);
        Sound reviveSound = parseSound(cfg.getString("sounds.revive.sound", "ENTITY_PLAYER_LEVELUP"));
        float reviveSoundVolume = (float) Math.max(0.0, cfg.getDouble("sounds.revive.volume", 0.5));
        float reviveSoundPitch = (float) Math.max(0.0, cfg.getDouble("sounds.revive.pitch", 1.2));
        String reloadPermission = cfg.getString("admin.reload-permission", "knockout.admin.reload");
        PotionType[] selfRevivePotionTypes = new PotionType[] {PotionType.HEALING, PotionType.REGENERATION};

        return new KnockoutConfig(
                knockoutTicks,
                rescueTicks,
                horizontalRescueRadius,
                rescuerMinYOffset,
                allowEnv,
                anyLethalTriggersKnockout,
                repeatKnockoutCooldownMs,
                allowMobKnockout,
                allowFinisherHit,
                teamOnlyRescue,
                postReviveInvulnerabilityTicks,
                selfReviveEnabled,
                selfReviveChance,
                selfReviveHealingChance,
                selfReviveRegenerationChance,
                selfReviveConsumePotion,
                reviveHealthMode,
                reviveHealthValue,
                reviveMinimumHearts,
                slownessAmplifier,
                slownessRefreshDurationTicks,
                blindnessAmplifier,
                blindnessRefreshDurationTicks,
                crawlEnabled,
                crawlSlownessAmplifier,
                crawlPose,
                onlyPlayers,
                notKnocked,
                noPermission,
                reloadSuccess,
                selfReviveSuccess,
                knockoutTitle,
                knockoutSubtitle,
                rescueResetTitle,
                rescueResetSubtitle,
                rescueProgressTitleTemplate,
                rescueProgressSubtitle,
                rescuerActionbarTemplate,
                revivedTitle,
                rescueParticlesEnabled,
                rescueParticle,
                rescueParticleEveryTicks,
                rescueParticleCount,
                rescueParticleOffsetX,
                rescueParticleOffsetY,
                rescueParticleOffsetZ,
                rescueParticleYAdd,
                reviveSoundEnabled,
                reviveSound,
                reviveSoundVolume,
                reviveSoundPitch,
                reloadPermission,
                selfRevivePotionTypes,
                locale);
    }

    private static String message(FileConfiguration cfg, String locale, String key) {
        String path = "messages." + key;
        String value = cfg.getString(path);
        if (value != null) {
            return value;
        }
        return defaultMessage(locale, key);
    }

    private static String defaultMessage(String locale, String key) {
        boolean ru = "ru".equalsIgnoreCase(locale);
        return switch (key) {
            case "only-players" -> ru ? "&cТолько для игроков." : "&cPlayers only.";
            case "not-knocked" -> ru ? "&cВы не в нокауте." : "&cYou are not knocked out.";
            case "no-permission" -> ru ? "&cНедостаточно прав." : "&cYou do not have permission.";
            case "reload-success" -> ru ? "&aКонфиг SPknockout перезагружен." : "&aSPknockout config reloaded.";
            case "self-revive-success" -> ru
                    ? "&aЗелье восстановления сработало, вы пришли в себя!"
                    : "&aYour healing potion worked, you recovered!";
            case "knockout-title" -> ru ? "&cВы упали в нокаут!" : "&cYou are knocked out!";
            case "knockout-subtitle" -> ru ? "&7Вы можете сдаться через /die" : "&7You can surrender with /die";
            case "rescue-reset-title" -> ru ? "&cВы упали в нокаут!" : "&cYou are knocked out!";
            case "rescue-reset-subtitle" -> ru ? "&7Вы можете сдаться через /die" : "&7You can surrender with /die";
            case "rescue-progress-title" -> ru ? "&bВосстановление: {percent}%" : "&bRecovery: {percent}%";
            case "rescue-progress-subtitle" -> ru ? "&7Не двигайтесь..." : "&7Do not move...";
            case "rescuer-actionbar" -> ru ? "&aСпасение: {percent}%" : "&aRescue: {percent}%";
            case "revived-title" -> ru ? "&aВы пришли в себя!" : "&aYou are back up!";
            default -> "";
        };
    }

    int effectiveKnockoutSlownessAmplifier() {
        return crawlEnabled ? crawlSlownessAmplifier : slownessAmplifier;
    }

    Component formatProgressTitle(int percent) {
        return deserialize(rescueProgressTitleTemplate.replace("{percent}", Integer.toString(percent)));
    }

    Component formatRescuerActionbar(int percent) {
        return deserialize(rescuerActionbarTemplate.replace("{percent}", Integer.toString(percent)));
    }

    private static Component deserialize(String input) {
        return LEGACY.deserialize(input == null ? "" : input);
    }

    private static Particle parseParticle(String input) {
        if (input == null || input.isBlank()) {
            return Particle.HEART;
        }
        try {
            return Particle.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Particle.HEART;
        }
    }

    private static Sound parseSound(String input) {
        if (input == null || input.isBlank()) {
            return Sound.ENTITY_PLAYER_LEVELUP;
        }
        try {
            return Sound.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Sound.ENTITY_PLAYER_LEVELUP;
        }
    }

    private static Pose parsePose(String input) {
        if (input == null || input.isBlank()) {
            return Pose.SWIMMING;
        }
        try {
            return Pose.valueOf(input.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Pose.SWIMMING;
        }
    }
}
