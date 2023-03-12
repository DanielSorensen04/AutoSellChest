package me.dalot.hooks;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    private static Economy econ = null;

    public static boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        econ = (Economy)rsp.getProvider();
        return (econ != null);
    }

    public static Economy getEconomy() {
        return econ;
    }
}
