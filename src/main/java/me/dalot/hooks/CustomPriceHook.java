package me.dalot.hooks;

import me.dalot.AutoSellChest;
import org.bukkit.inventory.ItemStack;

public class CustomPriceHook {

    public static double getCustomPrice(ItemStack item) {
        String type = item.getType().name();
        if (AutoSellChest.getPlugin().getConfig().get("CustomPrices." + type) != null)
            return AutoSellChest.getPlugin().getConfig().getDouble("CustomPrices." + type + ".Price") * item.getAmount();
        return -1.0D;
    }
}