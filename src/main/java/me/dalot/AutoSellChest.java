package me.dalot;

import me.dalot.commands.MainCommand;
import me.dalot.enums.HoloType;
import me.dalot.classes.SellChest;
import me.dalot.handlers.ChestHandler;
import me.dalot.hooks.VaultHook;
import me.dalot.listeners.*;
import me.dalot.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class AutoSellChest extends JavaPlugin {

    private static Plugin pl;

    private static ArrayList<SellChest> sellChests = new ArrayList<>();

    public static int remTime;

    private static HoloType holoType;

    public void onEnable() {
        pl = this;
        getServer().getConsoleSender().sendMessage("AutoSellChest");
        ConfigManager.startConfig();
        setHoloType();
        if (!VaultHook.setupEconomy()) {
            getServer().getConsoleSender().sendMessage(String.format("[%s] - Deaktiveret på grund af intet Vault dependecy blev fundet!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin((Plugin) this);
            return;
        }
        getServer().getPluginManager().registerEvents((Listener) new ChestClickEvent(), pl);
        getServer().getPluginManager().registerEvents((Listener) new ChestPlaceEvent(), pl);
        getServer().getPluginManager().registerEvents((Listener) new ChestBreakEvent(), pl);
        getServer().getPluginManager().registerEvents((Listener) new MenuClickEvent(), pl);
        getServer().getPluginManager().registerEvents((Listener) new MenuCloseEvent(), pl);
        ChestHandler.createChestItem();
        ChestHandler.startRemainingTime();
        getServer().getPluginCommand("asc").setExecutor((CommandExecutor) new MainCommand());
        getServer().getConsoleSender().sendMessage("[AutoSellChest] Registrerer event & plugin listeners.]");
        ConfigManager.loadAllChestFromData();
        getServer().getConsoleSender().sendMessage("[AutoSellChest] AutoSellChest er nu aktiveret" + getDescription().getVersion());
    }

    public void onDisable() {
        ConfigManager.saveData();
        for (SellChest chest : sellChests)
            chest.deleteHologram();
    }

    public void setHoloType() {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("DecentHolograms")) {
            holoType = HoloType.DECENTHOLOGRAMS;
        } else if (Bukkit.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            holoType = HoloType.HOLOGRAPHICDISPLAYS;
        } else {
            Bukkit.getConsoleSender().sendMessage("[AutoSellChest] Intet hologram plugin blev fundet på serveren.");
            holoType = null;
        }
    }

    public static ArrayList<SellChest> getSellChests() {
        return sellChests;
    }

    public static Plugin getPlugin() {
        return pl;
    }

    public static HoloType getHoloType() {
        return holoType;
    }
}