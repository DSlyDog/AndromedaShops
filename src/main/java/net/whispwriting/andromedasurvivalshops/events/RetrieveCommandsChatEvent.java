package net.whispwriting.andromedasurvivalshops.events;

import net.whispwriting.andromedasurvivalshops.guis.AdvancedShop;
import net.whispwriting.andromedasurvivalshops.guis.Shop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class RetrieveCommandsChatEvent implements Listener {

    private List<String> commands = new ArrayList<>();
    private List<String> info;
    private Player player;
    private AdvancedShop shop;
    private String item, name;
    private double price, sellPrice;

    public RetrieveCommandsChatEvent(Player player, List<String> info, AdvancedShop shop, String item, String name, double price, double sellPrice){
        this.player = player;
        this.info = info;
        this.shop = shop;
        this.item = item;
        this.price = price;
        this.sellPrice = sellPrice;
        this.name = name;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        if (event.getPlayer() == player) {
            event.setCancelled(true);
            String message = event.getMessage();
            if (message.equals("finish")){
                HandlerList.unregisterAll(this);
                boolean success = shop.addItem(item, name, price, sellPrice, commands, info);
                if (success)
                    event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Item successfully added.");
                else
                    event.getPlayer().sendMessage(ChatColor.RED + "Failed to add item. Make sure the item type is correct.");
            }else{
                player.sendMessage(ChatColor.GREEN + "Command added.");
                commands.add(event.getMessage());
            }
        }
    }

}
