package net.whispwriting.andromedasurvivalshops.guis;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class UIItemData {

    private ItemStack item;
    private String id;
    private double price;
    private int page;
    private int index;

    public UIItemData(ItemStack item, String id, double price, int page, int index){
        this.item = item;
        this.id = id;
        this.price = price;
        this.page = page;
        this.index = index;
    }

    public ItemStack getItem(){
        return item;
    }

    public String getID(){
        return id;
    }

    public double getPrice(){
        return price;
    }

    public boolean isOnPage(int page){
        return this.page == page;
    }

    public int getIndex(){
        return index;
    }

    public int getPage(){
        return page;
    }

}

