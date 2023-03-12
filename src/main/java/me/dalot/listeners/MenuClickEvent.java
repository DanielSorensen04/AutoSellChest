package me.dalot.listeners;

import me.dalot.classes.SellChest;
import me.dalot.gui.MenuHandler;
import me.dalot.handlers.ChestHandler;
import me.dalot.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class MenuClickEvent implements Listener {

    @EventHandler
    public void menuClick(InventoryClickEvent e) {
        if (e.getCurrentItem() != null &&
                e.getInventory() != null) {
            InventoryView invView = e.getView();
            String chestMenu = ConfigManager.getstringlang("Menus.ChestMenu.Title");
            String manageItemsMenu = ConfigManager.getstringlang("Menus.ManageItems.Title");
            Player p = (Player)e.getWhoClicked();
            ItemStack clickedItem = e.getCurrentItem();
            if (menuTitle(invView).equalsIgnoreCase(chestMenu)) {
                e.setCancelled(true);
                if (!clickedItem.hasItemMeta())
                    return;
                String clickedName = clickedItem.getItemMeta().getDisplayName();
                String startItem = ConfigManager.getstringlang("Menus.ChestMenu.Items.Start.Name");
                String stopItem = ConfigManager.getstringlang("Menus.ChestMenu.Items.Stop.Name");
                String manageItems = ConfigManager.getstringlang("Menus.ChestMenu.Items.ManageItems.Name");
                String moneyItem = ConfigManager.getstringlang("Menus.ChestMenu.Items.Money.Name");
                SellChest chest = ChestHandler.getSellChestFromList(p.getUniqueId().toString());
                if (chest == null) {
                    p.closeInventory();
                    return;
                }
                if (clickedName.equalsIgnoreCase(startItem)) {
                    chest.setStatus(true);
                    ItemStack stop = MenuHandler.createItem("Stop", "ChestMenu");
                    e.getInventory().setItem(e.getSlot(), stop);
                    ConfigManager.sendMessage((CommandSender)p, false, "StartedSelling");
                } else if (clickedName.equalsIgnoreCase(stopItem)) {
                    chest.setStatus(false);
                    ItemStack start = MenuHandler.createItem("Start", "ChestMenu");
                    e.getInventory().setItem(e.getSlot(), start);
                    ConfigManager.sendMessage((CommandSender)p, false, "StoppedSelling");
                } else if (clickedName.equalsIgnoreCase(manageItems)) {
                    p.openInventory(chest.getContentsInventory());
                } else if (clickedName.equalsIgnoreCase(moneyItem)) {
                    Bukkit.getServer().dispatchCommand((CommandSender)p, "asc collect");
                    p.closeInventory();
                }
            }
            if (menuTitle(invView).equalsIgnoreCase(manageItemsMenu)) {
                if (!clickedItem.hasItemMeta())
                    return;
                String clickedName = clickedItem.getItemMeta().getDisplayName();
                String manageItems = ConfigManager.getstringlang("Menus.ManageItems.Items.ManageItems.Name");
                if (clickedName.equalsIgnoreCase(manageItems))
                    e.setCancelled(true);
            }
        }
    }

    public String menuTitle(InventoryView invView) {
        return invView.getTitle();
    }
}
