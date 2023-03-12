package me.dalot.hooks;

import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ShopGUIHook implements Listener {

    public static double getShopGUIPrice(ItemStack item) {
        double price = ShopGuiPlusApi.getItemStackPriceSell(item);
        if (price == 0.0D)
            return -1.0D;
        return price;
    }
}
