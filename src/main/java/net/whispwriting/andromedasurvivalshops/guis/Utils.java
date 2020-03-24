package net.whispwriting.andromedasurvivalshops.guis;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {

    public static String chat(String s){
        s = s.replace("&", "§");
        return s;
    }

    public static UIItemData createItem(String type, String id, double price, int page, int index, List<UIItemData> items){
        try {
            ItemStack item;
            List<String> lore = new ArrayList<>();
            Material material;
            try {
                material = Material.getMaterial(type.toUpperCase());
            }catch(IllegalArgumentException e){
                return null;
            }
            item = new ItemStack(material, 1);

            ItemMeta meta = item.getItemMeta();
            lore.add("Price: $" + price);
            meta.setLore(lore);
            meta.setLocalizedName(id);
            item.setItemMeta(meta);
            UIItemData i = new UIItemData(item, type, price, page, index);
            items.add(i);
            return i;
        }catch(NullPointerException e){
            return null;
        }
    }

    public static ItemStack createItem(Material material, double price){
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("Price: $" + price);
        meta.setLocalizedName("buying");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, String name, String id){
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setLocalizedName(id);
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

}

