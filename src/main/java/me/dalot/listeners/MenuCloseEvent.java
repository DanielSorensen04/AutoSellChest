package me.dalot.listeners;


import me.dalot.classes.SellChest;
import me.dalot.handlers.AudienceHandler;
import me.dalot.handlers.ChestHandler;
import me.dalot.handlers.PriceHandler;
import me.dalot.managers.ConfigManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MenuCloseEvent implements Listener {

    @EventHandler
    public void saveItemsToData(InventoryCloseEvent e) {
        Player p = (Player)e.getPlayer();
        if (e.getInventory() != null) {
            String manageItemsMenu = ConfigManager.getstringlang("Menus.ManageItems.Title");
            InventoryView invView = e.getView();
            Inventory inv = e.getInventory();
            if (menuTitle(invView).equalsIgnoreCase(manageItemsMenu)) {
                int manageItemsSlot = ConfigManager.getLang().getInt("Menus.ManageItems.Items.ManageItems.Slot");
                SellChest chest = ChestHandler.getSellChestFromList(p.getUniqueId().toString());
                ArrayList<ItemStack> contents = new ArrayList<>();
                for (int i = 0; i < inv.getSize(); i++) {
                    if (i != manageItemsSlot &&
                            inv.getItem(i) != null &&
                            !inv.getItem(i).getType().equals(Material.AIR)) {
                        ItemStack item = inv.getItem(i);
                        double priceItem = PriceHandler.getPrice(item);
                        if (priceItem > 0.0D) {
                            contents.add(item);
                        } else {
                            inv.removeItem(new ItemStack[] { item });
                            p.getInventory().addItem(new ItemStack[] { item });
                        }
                    }
                }
                if (!chest.getContents().equals(contents)) {
                    chest.setContents(contents);
                    ConfigManager.sendMessage((CommandSender)p, false, "ItemsSaved");
                    String title = ConfigManager.getstringlang("ItemsAddedOrRemovedTitle.Title");
                    String subtitle = ConfigManager.getstringlang("ItemsAddedOrRemovedTitle.SubTitle");
                    AudienceHandler.sendTitle(p, title, subtitle);
                    AudienceHandler.sendSound("ClosedManageItemsMenuSound", p);
                }
            }
        }
    }

    public String menuTitle(InventoryView invView) {
        return invView.getTitle();
    }
}

