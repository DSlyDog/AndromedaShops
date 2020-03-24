package net.whispwriting.andromedasurvivalshops.events;

import net.whispwriting.andromedasurvivalshops.AndromedaShops;
import net.whispwriting.andromedasurvivalshops.guis.Shop;
import net.whispwriting.andromedasurvivalshops.guis.UIItemData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class ShopInteract implements Listener {

    private AndromedaShops plugin;
    private String uuid;
    private Shop shop;

    public ShopInteract(AndromedaShops plugin, String uuid, Shop shop){
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
            ItemStack item = e.getCurrentItem();
            String id = item.getItemMeta().getLocalizedName();
            UIItemData itemData = shop.getItem(id);
            if (itemData != null) {
                e.setCancelled(true);
                shop.clicked((Player) e.getWhoClicked(), item, plugin);
            }else if (id.equals("previousPage") || id.equals("nextPage") || id.equals("pageNum")){
                e.setCancelled(true);
                shop.clicked((Player) e.getWhoClicked(), item, plugin);
            }
        }catch(NullPointerException err){
            // do nothing
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if (!uuid.equals(e.getPlayer().getUniqueId().toString())){
            return;
        }
        HandlerList.unregisterAll(ShopInteract.this);
    }

}

