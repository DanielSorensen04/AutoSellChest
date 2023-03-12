package me.dalot.classes;


import eu.decentsoftware.holograms.api.DHAPI;
import me.dalot.AutoSellChest;
import me.dalot.enums.HoloType;
import me.dalot.gui.ManageItemsMenu;
import me.dalot.handlers.PriceHandler;
import me.dalot.managers.ConfigManager;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class SellChest {
    public ArrayList<ItemStack> contents;

    public Location loc;

    public UUID owner;

    public String ownerName;

    public boolean status;

    public Inventory itemsInventory;
 
    public SellChest(ArrayList<ItemStack> contents, Location loc, UUID owner, String ownerName, boolean status) {
        this.contents = contents;
        this.loc = loc;
        this.owner = owner;
        this.ownerName = ownerName;
        this.status = status;
        this.itemsInventory = ManageItemsMenu.getManageItemsMenu(this);
    }

    public void placeHologram() {
        String holoName;
        HolographicDisplaysAPI holoApi;
        Hologram holo;
        double holoHeight = ConfigManager.getConfig().getDouble("HologramHeight");
        Location loc = getLoc().clone().add(0.5D, holoHeight, 0.5D);
        List<String> tempLines = ConfigManager.getLangList("HologramLines");
        ArrayList<String> lines = new ArrayList<>();
        int itemSize = 0;
        if (getContents() != null)
            for (ItemStack item : getContents()) {
                if (item != null)
                    itemSize += item.getAmount();
            }
        for (String line : tempLines) {
            if (line.contains("%status%"))
                if (isWorking()) {
                    line = line.replace("%status%", ConfigManager.getstringlang("Status.working"));
                } else {
                    line = line.replace("%status%", ConfigManager.getstringlang("Status.stopped"));
                }
            if (line.contains("%time%"))
                line = line.replace("%time%", getTimePlaceHolder());
            line = line.replace("%items%", itemSize + "");
            if (line.contains("%money%"))
                line = line.replace("%money%", df.format(getMoney()) + "");
            lines.add(line);
        }
        HoloType type = AutoSellChest.getHoloType();
        switch (type) {
            case DECENTHOLOGRAMS:
                holoName = getOwnerName() + "-SellChest";
                if (DHAPI.getHologram(holoName) != null)
                    DHAPI.getHologram(holoName).delete();
                DHAPI.createHologram(holoName, loc, lines);
                break;
            case HOLOGRAPHICDISPLAYS:
                holoApi = HolographicDisplaysAPI.get(AutoSellChest.getPlugin());
                for (Hologram tempHolo : holoApi.getHolograms()) {
                    if (tempHolo.getPosition().distance(tempHolo.getPosition()) < 0.5D) {
                        tempHolo.delete();
                        break;
                    }
                }
                holo = holoApi.createHologram(loc);
                for (String line : lines)
                    holo.getLines().appendText(line);
                break;
        }
    }

    public void updateHologram() {
        placeHologram();
    }

    public void deleteHologram() {
        String holoName;
        HolographicDisplaysAPI holoApi;
        HoloType type = AutoSellChest.getHoloType();
        switch (type) {
            case DECENTHOLOGRAMS:
                holoName = this.ownerName + "-SellChest";
                if (DHAPI.getHologram(holoName) != null)
                    DHAPI.getHologram(holoName).delete();
                break;
            case HOLOGRAPHICDISPLAYS:
                holoApi = HolographicDisplaysAPI.get(AutoSellChest.getPlugin());
                for (Hologram holo : holoApi.getHolograms()) {
                    if (holo.getPosition().distance(holo.getPosition()) < 0.5D) {
                        holo.delete();
                        break;
                    }
                }
                break;
        }
    }

    public String getTimePlaceHolder() {
        String finalTime = "";
        int remTime = AutoSellChest.remTime;
        if (remTime / 3600 == 1) {
            finalTime = finalTime + (remTime / 3600) + " " + ConfigManager.getstringlang("TimeNames.Hour") + " ";
        } else if (remTime / 3600 > 1) {
            finalTime = finalTime + (remTime / 3600) + " " + ConfigManager.getstringlang("TimeNames.Hours") + " ";
        }
        if (remTime / 60 == 1) {
            finalTime = finalTime + (remTime / 60) + " " + ConfigManager.getstringlang("TimeNames.Minute") + " ";
        } else if (remTime / 60 > 1) {
            finalTime = finalTime + (remTime / 60) + " " + ConfigManager.getstringlang("TimeNames.Minutes") + " ";
        }
        if (remTime % 60 == 1) {
            finalTime = finalTime + (remTime % 60) + " " + ConfigManager.getstringlang("TimeNames.Second") + " ";
        } else if (remTime % 60 > 1) {
            finalTime = finalTime + (remTime % 60) + " " + ConfigManager.getstringlang("TimeNames.Seconds") + " ";
        } else if (remTime == 0) {
            finalTime = finalTime + ConfigManager.getstringlang("TimeNames.Now");
        }
        return finalTime;
    }

    public ArrayList<ItemStack> getContents() {
        return this.contents;
    }

    public void setContents(ArrayList<ItemStack> contents) {
        ConfigManager.getData().set(this.owner.toString() + ".Contents", contents);
        this.contents = contents;
        placeHologram();
    }

    public void setContents(Inventory contents) {
        ArrayList<ItemStack> newContents = new ArrayList<>();
        for (ListIterator<ItemStack> listIterator = contents.iterator(); listIterator.hasNext(); ) {
            ItemStack item = listIterator.next();
            if (item != null &&
                    !item.getType().equals(Material.AIR) &&
                    !item.isSimilar(ManageItemsMenu.manageItemsItem()) &&
                    PriceHandler.getPrice(item) > 0.0D)
                newContents.add(item);
        }
        ConfigManager.getData().set(this.owner.toString() + ".Contents", newContents);
        this.contents = newContents;
        updateHologram();
    }

    public Location getLoc() {
        return this.loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public double getMoney() {
        return ConfigManager.getData().getDouble(this.owner.toString() + ".Money");
    }

    public void setMoney(double money) {
        ConfigManager.getData().set(this.owner.toString() + ".Money", Double.valueOf(money));
        updateHologram();
    }

    public UUID getOwner() {
        return this.owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public boolean isWorking() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
        ConfigManager.getData().set(this.owner.toString() + ".Working", Boolean.valueOf(status));
        placeHologram();
    }

    public Inventory getContentsInventory() {
        return this.itemsInventory;
    }
}
