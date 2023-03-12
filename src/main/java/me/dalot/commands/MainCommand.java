package me.dalot.commands;

import me.dalot.AutoSellChest;
import me.dalot.classes.SellChest;
import me.dalot.handlers.AudienceHandler;
import me.dalot.handlers.ChestHandler;
import me.dalot.hooks.VaultHook;
import me.dalot.managers.ConfigManager;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class MainCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("asc")) {
            if (sender instanceof Player) {
                Player p = (Player)sender;
                Plugin pl = AutoSellChest.getPlugin();
                if (args.length == 0) {
                    for (String mes : ConfigManager.getLang().getStringList("HelpCommand"))
                        ConfigManager.sendMessage((CommandSender)p, mes.replace("&", ""));
                    return true;
                }
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("get")) {
                        if (sender.hasPermission("asc.get")) {
                            double price = pl.getConfig().getDouble("ChestPrice");
                            if (price == 0.0D) {
                                p.getInventory().addItem(new ItemStack[] { ChestHandler.getChestItem() });
                                ConfigManager.sendMessage((CommandSender)p, false, "BoughtChest");
                            } else {
                                double balance = VaultHook.getEconomy().getBalance((OfflinePlayer)p);
                                if (balance >= price) {
                                    HashMap<Integer, ItemStack> hash = p.getInventory().addItem(new ItemStack[] { ChestHandler.getChestItem() });
                                    if (hash != null) {
                                        VaultHook.getEconomy().withdrawPlayer((OfflinePlayer)p, price);
                                        ConfigManager.sendMessage((CommandSender)p, false, "BoughtChest");
                                    } else {
                                        String title = ConfigManager.getstringlang("NoSpaceTitle.Title");
                                        String subtitle = ConfigManager.getstringlang("NoSpaceTitle.SubTitle");
                                        AudienceHandler.sendTitle(p, title, subtitle);
                                        AudienceHandler.sendSound("NoSpaceSound", p);
                                    }
                                } else {
                                    ConfigManager.sendMessage((CommandSender)p, false, "NotEnoughMoney");
                                }
                            }
                        } else {
                            ConfigManager.sendMessage((CommandSender)p, false, "NoPermission");
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("delete")) {
                        SellChest chest = ChestHandler.getSellChestFromData(p.getUniqueId().toString());
                        if (chest != null) {
                            chest.deleteHologram();
                            chest.getLoc().getBlock().setType(Material.AIR);
                            ChestHandler.removeChestFromData(p.getUniqueId().toString());
                        } else {
                            ConfigManager.sendMessage((CommandSender)p, false, "YouDontHaveChest");
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("collect")) {
                        SellChest chest = ChestHandler.getSellChestFromData(p.getUniqueId().toString());
                        if (chest != null) {
                            double money = chest.getMoney();
                            if (money > 0.0D) {
                                String mes = ConfigManager.getstringlang("MoneyCollected").replace("%money%", money + "");
                                String title = ConfigManager.getstringlang("MoneyCollectedTitle.Title").replace("%money%", money + "");
                                String subtitle = ConfigManager.getstringlang("MoneyCollectedTitle.SubTitle").replace("%money%", money + "");
                                AudienceHandler.sendTitle(p, title, subtitle);
                                AudienceHandler.sendSound("MoneyCollectedSound", p);
                                ConfigManager.sendMessage((CommandSender)p, mes);
                                VaultHook.getEconomy().depositPlayer((OfflinePlayer)p, money);
                                chest.setMoney(0.0D);
                            } else {
                                ConfigManager.sendMessage((CommandSender)p, false, "NoMoneyToCollected");
                            }
                        } else {
                            ConfigManager.sendMessage((CommandSender)p, false, "YouDontHaveChest");
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("help")) {
                        for (String mes : ConfigManager.getConfig().getStringList("HelpCommand"))
                            ConfigManager.sendMessage((CommandSender)p, mes.replace("&", ""));
                        return true;
                    }
                } else if (args.length == 3 &&
                        args[0].equalsIgnoreCase("admin") &&
                        args[1].equalsIgnoreCase("delete")) {
                    if (p.hasPermission("asc.remove.player")) {
                        SellChest chest = ChestHandler.getSellChestFromName(args[2]);
                        if (chest != null) {
                            ChestHandler.removeChestFromData(chest.getOwner().toString());
                            String mesAdmin = ConfigManager.getstringlang("ChestRemovedAdmin").replace("%player%", chest.getOwnerName());
                            ConfigManager.sendMessage((CommandSender)p, mesAdmin);
                        } else {
                            String mes = ConfigManager.getstringlang("PlayerNotFound").replace("%player%", args[2]);
                            ConfigManager.sendMessage((CommandSender)p, mes);
                        }
                    } else {
                        ConfigManager.sendMessage((CommandSender)p, false, "NoPermission");
                    }
                    return true;
                }
            }
            for (String mes : ConfigManager.getConfig().getStringList("HelpCommand"))
                ConfigManager.sendMessage(sender, mes.replace("&", ""));
        }
        return false;
    }
}

