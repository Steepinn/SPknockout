package ru.knockout.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public final class KnockoutPlugin extends JavaPlugin implements CommandExecutor {

    private KnockoutManager manager;
    private KnockoutConfig knockoutConfig;

    @Override
    public void onEnable() {
        StartupBanner.print(this);
        reloadPluginConfig();
        getServer().getPluginManager().registerEvents(new KnockoutListener(manager), this);

        var die = getCommand("die");
        if (die != null) {
            die.setExecutor(this);
        } else {
            getLogger().severe("Команда die не объявлена в plugin.yml");
        }
        var knockout = getCommand("knockout");
        if (knockout != null) {
            knockout.setExecutor(this);
        } else {
            getLogger().severe("Команда knockout не объявлена в plugin.yml");
        }
        var recovery = getCommand("recovery");
        if (recovery != null) {
            recovery.setExecutor(this);
        } else {
            getLogger().severe("Команда recovery не объявлена в plugin.yml");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                manager.tick();
            }
        }.runTaskTimer(this, 1L, 1L);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("die")) {
            if (!command.getName().equalsIgnoreCase("knockout")) {
                if (!command.getName().equalsIgnoreCase("recovery")) {
                    return false;
                }
                if (!sender.hasPermission(knockoutConfig.recoveryPermission)) {
                    sender.sendMessage(knockoutConfig.noPermissionMessage);
                    return true;
                }
                if (args.length != 1) {
                    sender.sendMessage(knockoutConfig.recoveryUsageMessage);
                    return true;
                }
                Player target = getServer().getPlayerExact(args[0]);
                if (target == null || !target.isOnline()) {
                    sender.sendMessage(knockoutConfig.recoveryTargetNotFoundMessage);
                    return true;
                }
                if (!manager.recoveryCommand(target)) {
                    sender.sendMessage(knockoutConfig.recoveryTargetNotKnockedMessage);
                    return true;
                }
                sender.sendMessage(knockoutConfig.recoveryAdminSuccessMessage);
                target.sendMessage(knockoutConfig.recoveryTargetRecoveredMessage);
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission(knockoutConfig.reloadPermission)) {
                    sender.sendMessage(knockoutConfig.noPermissionMessage);
                    return true;
                }
                reloadPluginConfig();
                sender.sendMessage(knockoutConfig.reloadSuccessMessage);
                return true;
            }
            return false;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(knockoutConfig.onlyPlayersMessage);
            return true;
        }
        if (!manager.isKnockedOut(player)) {
            player.sendMessage(knockoutConfig.notKnockedMessage);
            return true;
        }
        manager.dieCommand(player);
        return true;
    }

    private void reloadPluginConfig() {
        saveDefaultConfig();
        reloadConfig();
        knockoutConfig = KnockoutConfig.from(getConfig());
        if (manager == null) {
            manager = new KnockoutManager(this, knockoutConfig);
        } else {
            manager.updateConfig(knockoutConfig);
        }
    }
}
