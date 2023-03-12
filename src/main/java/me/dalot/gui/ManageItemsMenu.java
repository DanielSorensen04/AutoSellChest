package me.dalot.gui;

import me.dalot.classes.SellChest;
import me.dalot.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
 
public class ManageItemsMenu {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static Inventory getManageItemsMenu(SellChest chest) {
        int size = ConfigManager.getLang().getInt("Menus.ManageItems.Size");
        Inventory inv = Bukkit.createInventory(null, size, ConfigManager.getstringlang("Menus.ManageItems.Title"));
        for (String menuItem : ConfigManager.getLang().getConfigurationSection("Menus.ManageItems.Items").getKeys(false)) {
            int slot = ConfigManager.getLang().getInt("Menus.ManageItems.Items." + menuItem + ".Slot");
            ItemStack item = MenuHandler.createItem(menuItem, "ManageItems");
            List<String> tempLore = item.getItemMeta().getLore();
            List<String> lore = new ArrayList<>();
            ItemMeta meta = item.getItemMeta();
            for (String lorePart : tempLore)
                lore.add(lorePart.replace("%money%", df.format(chest.getMoney()) + ""));
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot, item);
        }
        for (ItemStack item : chest.getContents()) {
            inv.addItem(new ItemStack[] { item });
        }
        return inv;
    }

    public static ItemStack manageItemsItem() {
        return MenuHandler.createItem("ManageItems", "ManageItems");
    }
}
