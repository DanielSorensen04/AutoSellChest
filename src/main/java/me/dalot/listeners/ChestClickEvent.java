package me.dalot.listeners;

import me.dalot.AutoSellChest;
import me.dalot.classes.SellChest;
import me.dalot.gui.ChestMenu;
import me.dalot.managers.ConfigManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class ChestClickEvent implements Listener {

    @EventHandler
    public void menuClickEvent(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        Player p = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
                block != null &&
                block.getType().equals(Material.CHEST))
            for (SellChest chest : AutoSellChest.getSellChests()) {
                if (chest.getLoc().equals(block.getLocation())) {
                    e.setCancelled(true);
                    if (chest.getOwner().equals(p.getUniqueId())) {
                        Inventory chestInv = ChestMenu.getChestMenu(chest);
                        p.openInventory(chestInv);
                    } else {
                        ConfigManager.sendMessage((CommandSender)p, false, "CantOpenThisChest");
                    }
                    return;
                }
            }
    }
}

