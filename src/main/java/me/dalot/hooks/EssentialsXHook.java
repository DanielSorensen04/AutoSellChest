package me.dalot.hooks;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public class EssentialsXHook {

    public static double getEssentialsPrice(ItemStack item) {
        Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        BigDecimal price = essentials.getWorth().getPrice((IEssentials)essentials, item);
        if (price == null)
            return -1.0D;
        return price.doubleValue() * item.getAmount();
    }
}

