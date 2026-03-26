package ru.knockout.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/** Баннер в консоли при старте (оформление через Adventure, как у крупных плагинов). */
public final class StartupBanner {

    private static final int INNER_WIDTH = 40;

    private StartupBanner() {}

    public static void print(JavaPlugin plugin) {
        String name = plugin.getPluginMeta().getName();
        String version = plugin.getPluginMeta().getVersion();

        var console = Bukkit.getConsoleSender();
        console.sendMessage(Component.empty());
        console.sendMessage(borderTop());
        console.sendMessage(padLine());
        console.sendMessage(titleLine(name));
        console.sendMessage(subLine(version));
        console.sendMessage(padLine());
        console.sendMessage(borderBottom());
        console.sendMessage(Component.empty());
    }

    private static Component borderTop() {
        return Component.text("╔" + "═".repeat(INNER_WIDTH) + "╗", NamedTextColor.GOLD);
    }

    private static Component borderBottom() {
        return Component.text("╚" + "═".repeat(INNER_WIDTH) + "╝", NamedTextColor.GOLD);
    }

    private static Component padLine() {
        return lineInner(Component.text(" ".repeat(INNER_WIDTH)));
    }

    private static Component titleLine(String pluginName) {
        Component core =
                Component.text("  ")
                        .append(Component.text("SP", NamedTextColor.YELLOW, TextDecoration.BOLD))
                        .append(Component.text("  ·  ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(pluginName, NamedTextColor.AQUA, TextDecoration.BOLD));
        return lineInner(fitted(core, INNER_WIDTH));
    }

    private static Component subLine(String version) {
        Component core =
                Component.text("  ")
                        .append(Component.text("v" + version, NamedTextColor.GRAY))
                        .append(Component.text("   ·   ", NamedTextColor.DARK_GRAY))
                        .append(Component.text("Steepin", NamedTextColor.DARK_AQUA))
                        .append(Component.text(" · ", NamedTextColor.DARK_GRAY))
                        .append(Component.text("SteepStudio", NamedTextColor.DARK_AQUA));
        return lineInner(fitted(core, INNER_WIDTH));
    }

    private static Component lineInner(Component inner) {
        return Component.text("║", NamedTextColor.GOLD).append(inner).append(Component.text("║", NamedTextColor.GOLD));
    }

    /**
     * Обрезает слишком длинную сериализованную строку, чтобы не ломать рамку в консоли.
     */
    private static Component fitted(Component content, int maxPlainWidth) {
        String plain = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(content);
        if (plain.length() <= maxPlainWidth) {
            int pad = maxPlainWidth - plain.length();
            return content.append(Component.text(" ".repeat(Math.max(0, pad))));
        }
        return Component.text(plain.substring(0, maxPlainWidth), NamedTextColor.GRAY);
    }
}
