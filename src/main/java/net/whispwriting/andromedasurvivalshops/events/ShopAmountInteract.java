package net.whispwriting.andromedasurvivalshops.events;

import net.whispwriting.andromedasurvivalshops.AylaShops;
import net.whispwriting.andromedasurvivalshops.guis.ShopAmount;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class ShopAmountInteract implements Listener {

    private AylaShops plugin;
    private String uuid;
    private ShopAmount shop;

    public ShopAmountInteract(AylaShops plugin, String uuid, ShopAmount shop){
        this.plugin = plugin;
        this.uuid = uuid;
        this.shop = shop;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if (!uuid.equals(e.getWhoClicked().getUniqueId().toString())){
            return;
        }
        try {
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            shop.clicked((Player) e.getWhoClicked(), item, plugin);
        }catch(NullPointerException err){
            // do nothing
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if (!uuid.equals(e.getPlayer().getUniqueId().toString())){
            return;
        }
        HandlerList.unregisterAll(ShopAmountInteract.this);
    }
}
