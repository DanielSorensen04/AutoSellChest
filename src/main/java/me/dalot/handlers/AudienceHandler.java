package me.dalot.handlers;

import me.dalot.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class AudienceHandler {

    public static void sendSound(String path, Player p) {
        if (ConfigManager.getConfig().get(path) != null) {
            String soundName = ConfigManager.getConfig().getString(path);
            try {
                Sound sound = Sound.valueOf(soundName);
                p.playSound(p.getLocation(), sound, 5.0F, 5.0F);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("- to find sound in " + path);
            }
        }
    }

    public static void sendTitle(Player p, String titleText, String subtitleText) {
        p.sendTitle(titleText, subtitleText, 10, 40, 10);
    }

    public static boolean isLegacy() {
        return (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") ||
                Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") ||
                Bukkit.getVersion().contains("1.12"));
    }
}
