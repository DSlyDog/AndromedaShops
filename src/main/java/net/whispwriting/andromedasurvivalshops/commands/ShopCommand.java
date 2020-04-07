package net.whispwriting.andromedasurvivalshops.commands;

import net.whispwriting.andromedasurvivalshops.AylaShops;
import net.whispwriting.andromedasurvivalshops.events.RetrieveInfoChatEvent;
import net.whispwriting.andromedasurvivalshops.events.ShopInteract;
import net.whispwriting.andromedasurvivalshops.events.ShopInteractAdvanced;
import net.whispwriting.andromedasurvivalshops.guis.AdvancedShop;
import net.whispwriting.andromedasurvivalshops.guis.Shop;
import net.whispwriting.andromedasurvivalshops.utils.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class ShopCommand implements CommandExecutor {

    private Map<String, Shop> shops;
    private Map<String, AdvancedShop> advancedShops;
    private AylaShops plugin;
    private SQL sql;

    public ShopCommand(Map<String, Shop> shops, Map<String, AdvancedShop> advancedShops, AylaShops plugin, SQL sql){
        this.shops = shops;
        this.plugin = plugin;
        this.sql = sql;
        this.advancedShops = advancedShops;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + "Only players may do that.");
            return true;
        }

        if ((args.length == 2 || args.length == 3) && args[0].equals("create")) {
            if (sender.hasPermission("AylaShop.shop.create")) {
                String name = args[1];
                Shop shop;
                if (!shops.containsKey(name)) {
                    if (args.length == 3)
                        shop = new Shop(name, args[2], sql);
                    else
                        shop = new Shop(name, "", sql);
                    shops.put(name, shop);
                    sender.sendMessage(ChatColor.DARK_GREEN + "Shop created.");
                } else {
                    sender.sendMessage(ChatColor.RED + "A shop with that name already exists.");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        } else if (args.length == 5 && args[0].equals("add")) {
            if (sender.hasPermission("AylaShop.shop.add")) {
                String shop = args[1];
                String item = args[2];
                double price, sellPrice;
                try {
                    price = Double.parseDouble(args[3]);
                    sellPrice = Double.parseDouble(args[4]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Buy and sell prices must be numbers.");
                    return true;
                }
                try {
                    boolean success = shops.get(shop).addItem(item, price, sellPrice);
                    if (success)
                        sender.sendMessage(ChatColor.DARK_GREEN + "Item successfully added.");
                    else
                        sender.sendMessage(ChatColor.RED + "Failed to add item. Make sure the item type is correct.");
                } catch (NullPointerException e) {
                    sender.sendMessage(ChatColor.RED + "No shop was found with that name.");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        } else if (args.length == 2 && args[0].equals("open")) {
            if (sender.hasPermission("AylaShop.shop.open")) {
                Shop shop = shops.get(args[1]);
                if (shop != null) {
                    Shop openShop = new Shop(shop.getName(), shop.getColors(), shop.getItems());
                    openShop.init();
                    Player player = (Player) sender;
                    Bukkit.getPluginManager().registerEvents(new ShopInteract(plugin, player.getUniqueId().toString(), openShop), plugin);
                    player.openInventory(openShop.GUI());
                } else {
                    sender.sendMessage(ChatColor.RED + "Could not find a shop with that name.");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        } else if (args.length == 3 && args[0].equals("open")) {
            if (sender.hasPermission("AylaShop.shop.open.others")) {
                Shop shop = shops.get(args[1]);
                if (shop != null) {
                    Shop openShop = new Shop(shop.getName(), shop.getColors(), shop.getItems());
                    openShop.init();
                    Player player = Bukkit.getPlayerExact(args[2]);
                    if (player != null) {
                        Bukkit.getPluginManager().registerEvents(new ShopInteract(plugin, player.getUniqueId().toString(), openShop), plugin);
                        player.openInventory(openShop.GUI());
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Could not find a shop with that name.");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        } else if (args.length == 2 && args[0].equals("fix")) {
            if (sender.hasPermission("AylaShop.shop.fix")) {
                Shop shop = shops.get(args[1]);
                if (shop != null) {
                    sql.fixIndex(args[1]);
                    shop.load();
                    sender.sendMessage(ChatColor.DARK_GREEN + "Indexes have been fixed.");
                }
            }
        } else if (args.length == 3 && args[0].equals("remove")) {
            //
        } else if (args.length == 2 && args[0].equals("delete")) {
            if (sender.hasPermission("AylaShop.shop.delete")) {
                Shop shop = shops.get(args[1]);
                if (shop != null) {
                    if (shop.delete()) {
                        shops.remove(args[1]);
                        sender.sendMessage(ChatColor.RED + "Shop deleted.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Failed to delete shop.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Could not find a shop with that name.");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        } else if (args.length == 1 && args[0].equals("list")) {
            if (sender.hasPermission("AylaShop.shops.list")) {
                sender.sendMessage(ChatColor.GOLD + "Shops:");
                StringBuilder builder = new StringBuilder();
                for (Map.Entry<String, Shop> shop : shops.entrySet())
                    builder.append(shop.getKey()).append(" ");
                sender.sendMessage(builder.toString());
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        } else if (args.length >= 1 && args[0].equals("advanced")){
            advancedCommand(sender, command, label, args);
        }else{
            sender.sendMessage(ChatColor.GOLD + "-------------- Andromeda Shops Help --------------");
            if (sender.hasPermission("AylaShop.shop.create"))
                sender.sendMessage(ChatColor.GOLD + "/shop create <name>                 Create a shop");
            if (sender.hasPermission("AylaShop.shop.add"))
                sender.sendMessage(ChatColor.GOLD + "/shop add <shop> <item> <price>   Add an item to a shop");
            if (sender.hasPermission("AylaShop.shop.remove"))
                sender.sendMessage(ChatColor.GOLD + "/shop remove <shop> <item>         Remove an item from a shop");
            if (sender.hasPermission("AylaShop.shop.delete"))
                sender.sendMessage(ChatColor.GOLD + "/shop delete <name>                 Delete a shop");
            if (sender.hasPermission("AylaShop.shop.fix"))
                sender.sendMessage(ChatColor.GOLD + "/shop fix <name>                 Remove accidental gaps in the positioning of items.");
            if (sender.hasPermission("AylaShop.shop.use"))
                sender.sendMessage(ChatColor.GOLD + "/shop open <shop>                   Open a shop");
        }
        return true;
    }

    private boolean advancedCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((args.length == 3 || args.length == 4) && args[1].equals("create")) {
            if (sender.hasPermission("AylaShop.shop.create")) {
                String name = args[2];
                AdvancedShop shop;
                if (!shops.containsKey(name)) {
                    if (args.length == 4)
                        shop = new AdvancedShop(name, args[3], sql);
                    else
                        shop = new AdvancedShop(name, "", sql);
                    advancedShops.put(name, shop);
                    sender.sendMessage(ChatColor.DARK_GREEN + "Shop created.");
                } else {
                    sender.sendMessage(ChatColor.RED + "A shop with that name already exists.");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        } else if (args.length == 6 && args[1].equals("add")) {
            if (sender.hasPermission("AylaShop.shop.add")) {
                String shopString = args[2];
                String item = args[3];
                double price, sellPrice;
                try {
                    price = Double.parseDouble(args[4]);
                    sellPrice = Double.parseDouble(args[5]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Buy and sell prices must be numbers.");
                    return true;
                }
                try {
                    AdvancedShop shop = advancedShops.get(shopString);
                    if (shop != null) {
                        sender.sendMessage(ChatColor.DARK_GREEN + "Please enter information for this item. Send a new message for each new line, " +
                                "and enter finished when you ae done.");
                        Bukkit.getPluginManager().registerEvents(new RetrieveInfoChatEvent((Player) sender, plugin, shop, item, price, sellPrice), plugin);
                    }
                } catch (NullPointerException e) {
                    sender.sendMessage(ChatColor.RED + "No shop was found with that name.");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        } else if (args.length == 7 && args[1].equals("add")) {
            if (sender.hasPermission("AylaShop.shop.add")) {
                String shopString = args[2];
                String item = args[3];
                String name = args[4];
                double price, sellPrice;
                try {
                    price = Double.parseDouble(args[5]);
                    sellPrice = Double.parseDouble(args[6]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Buy and sell prices must be numbers.");
                    return true;
                }
                try {
                    AdvancedShop shop = advancedShops.get(shopString);
                    if (shop != null) {
                        sender.sendMessage(ChatColor.DARK_GREEN + "Please enter information for this item. Send a new message for each new line, " +
                                "and enter finished when you ae done.");
                        Bukkit.getPluginManager().registerEvents(new RetrieveInfoChatEvent((Player) sender, plugin, shop, item, name, price, sellPrice), plugin);
                    }
                } catch (NullPointerException e) {
                    sender.sendMessage(ChatColor.RED + "No shop was found with that name.");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        } else if (args.length == 3 && args[1].equals("open")) {
            if (sender.hasPermission("AylaShop.shop.open")) {
                AdvancedShop shop = advancedShops.get(args[2]);
                if (shop != null) {
                    AdvancedShop openShop = new AdvancedShop(shop.getName(), shop.getColors(), shop.getItems());
                    openShop.init();
                    Player player = (Player) sender;
                    Bukkit.getPluginManager().registerEvents(new ShopInteractAdvanced(plugin, player.getUniqueId().toString(), openShop), plugin);
                    player.openInventory(openShop.GUI());
                } else {
                    sender.sendMessage(ChatColor.RED + "Could not find a shop with that name.");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        } else if (args.length == 3 && args[1].equals("fix")) {
            if (sender.hasPermission("AylaShop.shop.fix")) {
                AdvancedShop shop = advancedShops.get(args[2]);
                if (shop != null) {
                    sql.fixIndex(args[2]);
                    shop.load();
                    sender.sendMessage(ChatColor.DARK_GREEN + "Indexes have been fixed.");
                }
            }
        } else if (args.length == 4 && args[1].equals("open")) {
            if (sender.hasPermission("AylaShop.shop.open.others")) {
                AdvancedShop shop = advancedShops.get(args[1]);
                if (shop != null) {
                    AdvancedShop openShop = new AdvancedShop(shop.getName(), shop.getColors(), shop.getItems());
                    openShop.init();
                    Player player = Bukkit.getPlayerExact(args[3]);
                    if (player != null) {
                        Bukkit.getPluginManager().registerEvents(new ShopInteractAdvanced(plugin, player.getUniqueId().toString(), openShop), plugin);
                        player.openInventory(openShop.GUI());
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Could not find a shop with that name.");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        } else if (args.length == 4 && args[1].equals("remove")) {
            //
        } else if (args.length == 3 && args[1].equals("delete")) {
            if (sender.hasPermission("AylaShop.shop.delete")) {
                AdvancedShop shop = advancedShops.get(args[2]);
                if (shop != null) {
                    if (shop.delete()) {
                        shops.remove(args[2]);
                        sender.sendMessage(ChatColor.RED + "Shop deleted.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Failed to delete shop.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Could not find a shop with that name.");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        } else if (args.length == 2 && args[1].equals("list")) {
            if (sender.hasPermission("AylaShop.shops.list")) {
                sender.sendMessage(ChatColor.GOLD + "Shops:");
                StringBuilder builder = new StringBuilder();
                for (Map.Entry<String, AdvancedShop> shop : advancedShops.entrySet())
                    builder.append(shop.getKey()).append(" ");
                sender.sendMessage(builder.toString());
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        } else {
            sender.sendMessage(ChatColor.GOLD + "-------------- Andromeda Shops Help --------------");
            if (sender.hasPermission("AylaShop.shop.create"))
                sender.sendMessage(ChatColor.GOLD + "/shop advanced create <name>                 Create a shop");
            if (sender.hasPermission("AylaShop.shop.add"))
                sender.sendMessage(ChatColor.GOLD + "/shop advanced add <shop> <item> <price>   Add an item to a shop");
            if (sender.hasPermission("AylaShop.shop.remove"))
                sender.sendMessage(ChatColor.GOLD + "/shop advanced remove <shop> <item>         Remove an item from a shop");
            if (sender.hasPermission("AylaShop.shop.delete"))
                sender.sendMessage(ChatColor.GOLD + "/shop advanced delete <name>                 Delete a shop");
            if (sender.hasPermission("AylaShop.shop.fix"))
                sender.sendMessage(ChatColor.GOLD + "/shop advanced fix <name>                 Remove accidental gaps in the positioning of items.");
            if (sender.hasPermission("AylaShop.shop.use"))
                sender.sendMessage(ChatColor.GOLD + "/shop advanced open <shop>                   Open a shop");
        }
        return true;
    }
}
