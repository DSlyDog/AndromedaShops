package net.whispwriting.andromedasurvivalshops.commands;

import net.whispwriting.andromedasurvivalshops.AndromedaShops;
import net.whispwriting.andromedasurvivalshops.events.ShopInteract;
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
    private AndromedaShops plugin;
    private SQL sql;

    public ShopCommand(Map<String, Shop> shops, AndromedaShops plugin, SQL sql){
        this.shops = shops;
        this.plugin = plugin;
        this.sql = sql;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.DARK_RED + "Only players may do that.");
            return true;
        }

        if ((args.length == 2 || args.length == 3) && args[0].equals("create")){
            if (sender.hasPermission("Andromeda.shop.create")) {
                String name = args[1];
                Shop shop;
                if (!shops.containsKey(name)) {
                    if (args.length == 3)
                        shop = new Shop(name, args[2], sql);
                    else
                        shop = new Shop(name, "", sql);
                    shops.put(name, shop);
                    sender.sendMessage(ChatColor.DARK_GREEN + "Shop created.");
                }else{
                    sender.sendMessage(ChatColor.RED + "A shop with that name already exists.");
                }
            }else{
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        }else if (args.length == 4 && args[0].equals("add")){
            if (sender.hasPermission("Andromeda.shop.add")) {
                String shop = args[1];
                String item = args[2];
                double price;
                try {
                    price = Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Price must be a number.");
                    return true;
                }
                try {
                    boolean success = shops.get(shop).addItem(item, price);
                    if (success)
                        sender.sendMessage(ChatColor.DARK_GREEN + "Item successfully added.");
                    else
                        sender.sendMessage(ChatColor.RED + "Failed to add item. Make sure the item type is correct.");
                } catch (NullPointerException e) {
                    sender.sendMessage(ChatColor.RED + "No shop was found with that name.");
                }
            }else{
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        }else if (args.length == 2 && args[0].equals("open")) {
            if (sender.hasPermission("Andromeda.shop.open")) {
                Shop shop = shops.get(args[1]);
                if (shop != null) {
                    Shop openShop = new Shop(shop.getName(), shop.getColors(), shop.getItems());
                    openShop.init();
                    Player player = (Player) sender;
                    Bukkit.getPluginManager().registerEvents(new ShopInteract(plugin, player.getUniqueId().toString(), openShop), plugin);
                    player.openInventory(openShop.GUI());
                }else{
                    sender.sendMessage(ChatColor.RED + "Could not find a shop with that name.");
                }
            }else{
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        }else if (args.length == 3 && args[0].equals("open")){
            if (sender.hasPermission("Andromeda.shop.open.others")) {
                Shop shop = shops.get(args[1]);
                if (shop != null) {
                    Shop openShop = new Shop(shop.getName(), shop.getColors(), shop.getItems());
                    openShop.init();
                    Player player = Bukkit.getPlayerExact(args[2]);
                    if (player != null) {
                        Bukkit.getPluginManager().registerEvents(new ShopInteract(plugin, player.getUniqueId().toString(), openShop), plugin);
                        player.openInventory(openShop.GUI());
                    }
                }else{
                    sender.sendMessage(ChatColor.RED + "Could not find a shop with that name.");
                }
            }else{
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        }else if (args.length == 3 && args[0].equals("remove")){
            //
        }else if (args.length == 2 && args[0].equals("delete")){
            if (sender.hasPermission("Andromeda.shop.delete")) {
                Shop shop = shops.get(args[1]);
                if (shop != null) {
                    if (shop.delete()) {
                        shops.remove(args[1]);
                        sender.sendMessage(ChatColor.RED + "Shop deleted.");
                    }else{
                        sender.sendMessage(ChatColor.RED + "Failed to delete shop.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Could not find a shop with that name.");
                }
            }else{
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        }else if (args.length == 1 && args[0].equals("list")) {
            if (sender.hasPermission("Andromeda.shops.list")){
                sender.sendMessage(ChatColor.GOLD + "Shops:");
                StringBuilder builder = new StringBuilder();
                for (Map.Entry<String, Shop> shop : shops.entrySet())
                    builder.append(shop.getKey()).append(" ");
                sender.sendMessage(builder.toString());
            }else{
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
            }
        }else{
            sender.sendMessage(ChatColor.GOLD + "-------------- Andromeda Shops Help --------------");
            if (sender.hasPermission("Andromeda.shop.create"))
                sender.sendMessage(ChatColor.GOLD + "/shop create <name>                 Create a shop");
            if (sender.hasPermission("Andromeda.shop.add"))
                sender.sendMessage(ChatColor.GOLD + "/shop add <shop> <item> <price>   Add an item to a shop");
            if (sender.hasPermission("Andromeda.shop.remove"))
                sender.sendMessage(ChatColor.GOLD + "/shop remove <shop> <item>         Remove an item from a shop");
            if (sender.hasPermission("Andromeda.shop.delete"))
                sender.sendMessage(ChatColor.GOLD + "/shop delete <name>                 Delete a shop");
            if (sender.hasPermission("Andromeda.shop.use"))
                sender.sendMessage(ChatColor.GOLD + "/shop open <shop>                   Open a shop");
        }
        return true;
    }
}
