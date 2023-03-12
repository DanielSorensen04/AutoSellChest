package me.dalot.handlers;

import me.dalot.AutoSellChest;
import me.dalot.classes.SellChest;
import me.dalot.gui.ManageItemsMenu;
import me.dalot.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChestHandler {
    private static ItemStack chestItem;

    public static void createChestItem() {
        Material type = Material.getMaterial(ConfigManager.getstringlang("ChestItem.Material"));
        String name = ConfigManager.getstringlang("ChestItem.Name");
        List<String> lore = ConfigManager.getLangList("ChestItem.Lore");
        chestItem = new ItemStack(type);
        ItemMeta meta = chestItem.getItemMeta();
        meta.setDisplayName(name.replace("&", ""));
                meta.setLore(lore);
        chestItem.setItemMeta(meta);
    }

    public static ItemStack getChestItem() {
        return chestItem;
    }

    public static SellChest getSellChestFromData(String uuid) {
        ArrayList<ItemStack> contents;
        if (ConfigManager.getData().get(uuid) == null)
            return null;
        if (ConfigManager.getData().get(uuid + ".Contents") == null) {
            contents = null;
        } else {
            contents = (ArrayList<ItemStack>)ConfigManager.getData().get(uuid + ".Contents");
        }
        Location loc = (Location)ConfigManager.getData().get(uuid + ".Location");
        String ownerName = ConfigManager.getData().getString(uuid + ".OwnerName");
        boolean status = ConfigManager.getData().getBoolean(uuid + ".Working");
        return new SellChest(contents, loc, UUID.fromString(uuid), ownerName, status);
    }

    public static SellChest getSellChestFromName(String name) {
        for (String uuid : ConfigManager.getData().getConfigurationSection("").getKeys(false)) {
            String ownerName = ConfigManager.getData().getString(uuid + ".OwnerName");
            if (ownerName.equalsIgnoreCase(name))
                return getSellChestFromList(uuid);
        }
        return null;
    }

    public static SellChest getSellChestFromList(String uuid) {
        for (SellChest chest : AutoSellChest.getSellChests()) {
            if (chest.getOwner().toString().equalsIgnoreCase(uuid))
                return chest;
        }
        return null;
    }

    public static void removeChestFromData(String uuid) {
        Player p = Bukkit.getPlayer(UUID.fromString(uuid));
        if (ConfigManager.getData().get(uuid) == null) {
            if (p != null)
                ConfigManager.sendMessage((CommandSender)p, false, "YouDontHaveChest");
        } else {
            ConfigManager.getData().set(uuid, null);
            SellChest chest = getSellChestFromList(uuid);
            AutoSellChest.getSellChests().remove(chest);
            if (p != null)
                ConfigManager.sendMessage((CommandSender)p, false, "ChestRemoved");
        }
    }

    public static void saveChestToData(SellChest chest) {
        ArrayList<ItemStack> contents;
        Location loc = chest.getLoc();
        double money = chest.getMoney();
        String uuid = chest.getOwner().toString();
        String ownerName = chest.getOwnerName();
        boolean status = chest.isWorking();
        if (chest.getContents() != null) {
            contents = chest.getContents();
        } else {
            contents = null;
        }
        ConfigManager.getData().set(uuid + ".OwnerName", ownerName);
        ConfigManager.getData().set(uuid + ".Money", Double.valueOf(money));
        ConfigManager.getData().set(uuid + ".Location", loc);
        ConfigManager.getData().set(uuid + ".Contents", contents);
        ConfigManager.getData().set(uuid + ".Working", Boolean.valueOf(status));
    }

    public static void sellItemsInChests() {
        for (SellChest chest : AutoSellChest.getSellChests()) {
            if (chest.isWorking()) {
                double price = 0.0D;
                Inventory playerInv = chest.getContentsInventory();
                for (int i = 0; i < playerInv.getSize(); i++) {
                    ItemStack item = chest.getContentsInventory().getItem(i);
                    if (item != null &&
                            !item.isSimilar(ManageItemsMenu.manageItemsItem()) &&
                            !item.getType().equals(Material.AIR)) {
                        double priceItem = PriceHandler.getPrice(item);
                        if (priceItem > 0.0D) {
                            price += priceItem;
                            playerInv.setItem(i, null);
                        }
                    }
                }
                if (price > 0.0D) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(chest.getOwner());
                    if (p.isOnline()) {
                        String mes = ConfigManager.getstringlang("ItemsSold").replace("%money%", price + "");
                        ConfigManager.sendMessage((CommandSender)p, mes);
                    }
                    double chestBalance = chest.getMoney();
                    chest.setMoney(chestBalance + price);
                    chest.setContents(playerInv);
                }
            }
            chest.updateHologram();
        }
        ConfigManager.saveData();
    }

    public static void startRemainingTime() {
        final int holoUpdateTime = ConfigManager.getConfig().getInt("IntervalHoloReplacer");
        final int[] i = { 0 };
        (new BukkitRunnable() {
            public void run() {
                if (i[0] != 0) {
                    AutoSellChest.remTime -= holoUpdateTime;
                } else {
                    i[0] = i[0] + 1;
                }
                if (AutoSellChest.remTime <= 0) {
                    ChestHandler.sellItemsInChests();
                    AutoSellChest.remTime = ConfigManager.getConfig().getInt("SellInterval");
                }
                for (SellChest chest : AutoSellChest.getSellChests())
                    chest.updateHologram();
            }
        }).runTaskTimer(AutoSellChest.getPlugin(), 0L, holoUpdateTime * 20L);
    }
}

