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

    public static UIItemData createItem(String type, String id, double price, double sellPrice, int page, int index, List<UIItemData> items){
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
            lore.add("Buy: $" + price);
            lore.add("Sell: $" + sellPrice);
            meta.setLore(lore);
            meta.setLocalizedName(id);
            item.setItemMeta(meta);
            UIItemData i = new UIItemData(item, type, price, sellPrice, page, index);
            items.add(i);
            return i;
        }catch(NullPointerException e){
            return null;
        }
    }

    public static UIItemData createItem(String type, String id, double price, double sellPrice, int page, int index,
                                        List<UIItemData> items, List<String> commands, List<String> info){
        try {
            ItemStack item;
            Material material;
            try {
                material = Material.getMaterial(type.toUpperCase());
            }catch(IllegalArgumentException e){
                return null;
            }
            item = new ItemStack(material, 1);

            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("$" + price);
            if (sellPrice != 0)
                lore.add("$" + price);
            lore.addAll(info);
            meta.setLore(lore);
            if (id.equals("")) {
                meta.setLocalizedName(type);
            } else {
                meta.setLocalizedName(id);
                meta.setDisplayName(chat(id));
            }
            item.setItemMeta(meta);
            UIItemData i;
            if (id.equals(""))
                i = new UIItemData(item, type, price, sellPrice, page, index, commands, info);
            else
                i = new UIItemData(item, id, price, sellPrice, page, index, commands, info);
            items.add(i);
            return i;
        }catch(NullPointerException e){
            return null;
        }
    }

    public static ItemStack createItem(Material material, double price, double sellPrice){
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("Buy: $" + price);
        if (sellPrice != 0)
            lore.add("Sell: $" + sellPrice);
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

