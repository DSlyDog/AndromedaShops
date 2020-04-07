package net.whispwriting.andromedasurvivalshops.events;

import net.whispwriting.andromedasurvivalshops.AylaShops;
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

public class RetrieveInfoChatEvent implements Listener {

    private List<String> info = new ArrayList<>();
    private Player player;
    private AdvancedShop shop;
    private AylaShops plugin;
    private String item, name;
    private Double price, sellPrice;

    public RetrieveInfoChatEvent(Player player, AylaShops plugin, AdvancedShop shop, String item, double price, double sellPrice){
        this.player = player;
        this.plugin = plugin;
        this.shop = shop;
        this.item = item;
        this.price = price;
        this.sellPrice = sellPrice;
        this.name = "";
    }

    public RetrieveInfoChatEvent(Player player, AylaShops plugin, AdvancedShop shop, String item, String name, double price, double sellPrice){
        this.player = player;
        this.plugin = plugin;
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
                player.sendMessage(ChatColor.DARK_GREEN + "Please enter commands. When you are done, type finish.");
                Bukkit.getPluginManager().registerEvents(new RetrieveCommandsChatEvent(player, info, shop, item, name, price, sellPrice), plugin);
            }else{
                player.sendMessage(ChatColor.GREEN + "Line added.");
                info.add(event.getMessage());
            }
        }
    }
}
