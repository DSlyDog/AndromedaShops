package net.whispwriting.andromedasurvivalshops.guis;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class UIItemData {

    private ItemStack item;
    private String id;
    private double price, sellPrice;
    private int page;
    private int index;
    private List<String> commands;
    private List<String> lore;

    public UIItemData(ItemStack item, String id, double price, double sellPrice, int page, int index){
        this.item = item;
        this.id = id;
        this.price = price;
        this.page = page;
        this.index = index;
        this.sellPrice = sellPrice;
    }

    public UIItemData(ItemStack item, String id, double price, double sellPrice, int page, int index, List<String> commands, List<String> lore){
        this.item = item;
        this.id = id;
        this.price = price;
        this.page = page;
        this.index = index;
        this.sellPrice = sellPrice;
        this.commands = commands;
        this.lore = lore;
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

    public double getSellPrice(){
        return sellPrice;
    }

    public List<String> getCommands(){
        return commands;
    }

    public String getCommandsString(){
        StringBuilder builder = new StringBuilder();
        for (String s : commands){
            builder.append(s).append(",");
        }
        return builder.toString();
    }

    public List<String> getLore(){
        return lore;
    }

    public String getLoreString(){
        StringBuilder builder = new StringBuilder();
        for (String s : lore){
            builder.append(s).append(",");
        }
        return builder.toString();
    }

}

