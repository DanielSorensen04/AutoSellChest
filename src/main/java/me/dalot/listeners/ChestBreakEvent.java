package me.dalot.listeners;

import me.dalot.AutoSellChest;
import me.dalot.classes.SellChest;
import me.dalot.managers.ConfigManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ChestBreakEvent implements Listener {

    @EventHandler
    public void chestBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block block = e.getBlock();
        if (block.getType().equals(Material.CHEST))
            for (SellChest chest : AutoSellChest.getSellChests()) {
                if (chest.getLoc().equals(block.getLocation())) {
                    e.setCancelled(true);
                    if (p.getUniqueId().toString().equalsIgnoreCase(chest.getOwner().toString())) {
                        ConfigManager.sendMessage((CommandSender)p, false, "CantRemoveWithBreakOwner");
                    } else {
                        String mes = ConfigManager.getstringlang("CantRemoveWithBreakOther").replace("%player%", chest.getOwnerName());
                        ConfigManager.sendMessage((CommandSender)p, mes);
                    }
                    return;
                }
            }
    }
}
