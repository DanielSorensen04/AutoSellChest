package me.dalot.handlers;

import me.dalot.hooks.CustomPriceHook;
import me.dalot.hooks.EssentialsXHook;
import me.dalot.hooks.ShopGUIHook;
import me.dalot.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class PriceHandler {

    public static double getPrice(ItemStack item) {
        String priceSource = ConfigManager.getConfig().getString("PriceSource");
        if (priceSource.equalsIgnoreCase("Essentials"))
            return EssentialsXHook.getEssentialsPrice(item);
        if (priceSource.equalsIgnoreCase("ShopGUI+") || ConfigManager.getConfig().getString("PriceSource").equalsIgnoreCase("ShopGUIPlus"))
            return ShopGUIHook.getShopGUIPrice(item);
        if (priceSource.equalsIgnoreCase("Custom"))
            return CustomPriceHook.getCustomPrice(item);
        Bukkit.getConsoleSender().sendMessage("- hook is not recognized.");
        return 0.0D;
    }
}
