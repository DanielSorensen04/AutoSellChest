package me.dalot.gui;

import me.dalot.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MenuHandler {

    public static void fillMenu(String filler, Inventory inv) {
        try {
            Material mat = Material.getMaterial(filler);
            ItemStack fillerItem = new ItemStack(mat);
            ItemMeta meta = fillerItem.getItemMeta();
            meta.setDisplayName("§8");
                    fillerItem.setItemMeta(meta);
            for (int i = 0; i < inv.getSize(); i++)
                inv.setItem(i, fillerItem);
        } catch (Exception e) {
            Bukkit.getServer().getConsoleSender().sendMessage("in the Fill Menu: " + inv.getName() + "");
        }
    }

    public static ItemStack createItem(String path, String menu) {
        ItemStack item;
        String name = ConfigManager.getstringlang("Menus." + menu + ".Items." + path + ".Name");
        String block = ConfigManager.getstringlang("Menus." + menu + ".Items." + path + ".Block");
        List<String> tempLore = ConfigManager.getLang().getStringList("Menus." + menu + ".Items." + path + ".Lore");
        List<String> lore = new ArrayList<>();
        try {
            item = new ItemStack(Material.getMaterial(block));
        } catch (Exception e) {
            item = new ItemStack(Material.BEDROCK);
            Bukkit.getServer().getConsoleSender().sendMessage("in the: " + path + " item in the " + menu + " menu");
        }
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name.replace("&", ""));
        for (String lorePart : tempLore)
            lore.add(lorePart.replace("&", ""));
                    meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
