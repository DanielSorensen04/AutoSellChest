package me.dalot.managers;

import me.dalot.AutoSellChest;
import me.dalot.classes.SellChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConfigManager {
    private static File langFile;

    private static YamlConfiguration lang;

    private static File dataFile;

    private static YamlConfiguration data;

    private static Plugin pl;

    public static void startConfig() {
        pl = AutoSellChest.getPlugin();
        pl.saveDefaultConfig();
        createLang();
        createData();
        (new BukkitRunnable() {
            public void run() {
                ConfigManager.saveData();
            }
        }).runTaskTimer(pl, 0L, pl.getConfig().getLong("DataSaveInterval"));
    }

    public static void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createLang() {
        langFile = new File(pl.getDataFolder(), "lang.yml");
        lang = new YamlConfiguration();
        if (!langFile.exists())
            pl.saveResource("lang.yml", false);
        try {
            lang.load(langFile);
        } catch (IOException|NullPointerException|org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private static void createData() {
        dataFile = new File(pl.getDataFolder(), "data.yml");
        data = new YamlConfiguration();
        if (!dataFile.exists())
            pl.saveResource("data.yml", false);
        try {
            data.load(dataFile);
        } catch (IOException|NullPointerException|org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static String getString(String path) {
        try {
            return pl.getConfig().getString(path).replace("&", "");
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("- ERROR I CONFIG.YML PATH: " + path);
            return null;
        }
    }

    public static String getstringlang(String path) {
        try {
            return lang.getString(path).replace("&", "");
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("- ERROR I LANG.YML PATH: " + path);
            return null;
        }
    }

    public static void sendMessage(CommandSender sender, String mes) {
        String prefix = getstringlang("Prefix");
        sender.sendMessage(prefix + " " + mes);
    }

    public static void sendMessage(CommandSender sender, boolean config, String path) {
        String mes, prefix = getstringlang("Prefix");
        if (config) {
            mes = getString(path);
        } else {
            mes = getstringlang(path);
        }
        sender.sendMessage(prefix + " " + mes);
    }

    public static List<String> getLangList(String path) {
        List<String> tempList = lang.getStringList(path);
        ArrayList<String> holoLines = new ArrayList<>();
        for (String part : tempList)
            holoLines.add(part.replace("&", ""));
        return holoLines;
    }

    public static void loadAllChestFromData() {
        AutoSellChest.getSellChests().clear();
        for (String uuid : data.getConfigurationSection("").getKeys(false)) {
            ArrayList<ItemStack> contents;
            if (getData().get(uuid + ".Contents") == null) {
                contents = null;
            } else {
                contents = (ArrayList<ItemStack>)getData().get(uuid + ".Contents");
            }
            Location loc = (Location)getData().get(uuid + ".Location");
            String ownerName = getData().getString(uuid + ".OwnerName");
            boolean status = getData().getBoolean(uuid + ".Working");
            SellChest chest = new SellChest(contents, loc, UUID.fromString(uuid), ownerName, status);
            AutoSellChest.getSellChests().add(chest);
            chest.placeHologram();
        }
    }

    public static FileConfiguration getConfig() {
        return pl.getConfig();
    }

    public static YamlConfiguration getLang() {
        return lang;
    }

    public static YamlConfiguration getData() {
        return data;
    }
}