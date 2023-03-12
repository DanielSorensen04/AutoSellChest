package me.dalot.listeners;

import me.dalot.AutoSellChest;
import me.dalot.classes.SellChest;
import me.dalot.handlers.AudienceHandler;
import me.dalot.handlers.ChestHandler;
import me.dalot.managers.ConfigManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ChestPlaceEvent implements Listener {

    @EventHandler
    public void chestPlace(BlockPlaceEvent e) {
        if (e.getBlock() != null) {
            ItemStack item = e.getItemInHand();
            Player p = e.getPlayer();
            if (!e.isCancelled() &&
                    item.isSimilar(ChestHandler.getChestItem())) {
                Location loc = e.getBlockPlaced().getLocation();
                SellChest chest = ChestHandler.getSellChestFromData(p.getUniqueId().toString());
                if (chest != null) {
                    ConfigManager.sendMessage((CommandSender) p, false, "AlreadyHaveChest");
                    e.setCancelled(true);
                } else {
                    SellChest sellChest = new SellChest(new ArrayList(), loc, p.getUniqueId(), p.getName(), false);
                    sellChest.placeHologram();
                    AutoSellChest.getSellChests().add(sellChest);
                    ChestHandler.saveChestToData(sellChest);
                    ConfigManager.sendMessage((CommandSender) p, false, "ChestCreated");
                    String title = ConfigManager.getstringlang("ChestPlacedTitle.Title");
                    String subtitle = ConfigManager.getstringlang("ChestPlacedTitle.SubTitle");
                    AudienceHandler.sendTitle(p, title, subtitle);
                    AudienceHandler.sendSound("CreatedChestSound", p);
                }
            }
        }
    }
}
