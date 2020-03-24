package net.whispwriting.andromedasurvivalshops.guis;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import net.ess3.api.Economy;
import net.whispwriting.andromedasurvivalshops.AndromedaShops;
import net.whispwriting.andromedasurvivalshops.events.ShopAmountInteract;
import net.whispwriting.andromedasurvivalshops.files.ShopFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class ShopAmount {

    private Inventory inv;
    private String inventoryString;
    private int columns = 9;
    private int rows = columns * 6;
    private String name;
    private Material material;
    private int numberToBuy = 1;
    private double price;
    private ItemStack buying;
    private ItemStack buy;
    private ItemStack up;
    private ItemStack down;

    public ShopAmount(Material material, String name, double price){
        this.name = name;
        this.material = material;
        this.price = price;
    }

    public void init(){
        inventoryString = Utils.chat(name);
        inv = Bukkit.createInventory(null, rows, inventoryString);
        buying = Utils.createItem(material, price);
        buy = Utils.createItem(Material.BLUE_WOOL, "Purchase", "purchase");
        up = Utils.createItem(Material.GREEN_WOOL, "Increase amount to buy", "increase");
        down = Utils.createItem(Material.RED_WOOL, "Decrease amount to buy", "decrease");
        inv.setItem(22, buying);
        inv.setItem(38, down);
        inv.setItem(40, buy);
        inv.setItem(42, up);
    }

    public Inventory GUI(){
        return inv;
    }

    public void clicked(Player player, ItemStack clicked, AndromedaShops plugin){
        String id = clicked.getItemMeta().getLocalizedName();
        if (id.equals("increase")){
            incrementCounters(true);
            buying.setAmount(numberToBuy);
            inv.setItem(22, buying);
            inv.setContents(inv.getContents());
        }else if (id.equals("decrease")){
            incrementCounters(false);
            buying.setAmount(numberToBuy);
            inv.setItem(22, buying);
            inv.setContents(inv.getContents());
        }else if (id.equals("purchase")){
            if (canBuy(player)) {
                try {
                    Economy.substract(player.getName(), BigDecimal.valueOf(numberToBuy * price));
                    player.getInventory().addItem(new ItemStack(material, numberToBuy));
                    player.sendMessage(ChatColor.GREEN + "Payment successful. " + ChatColor.RED + "$" + BigDecimal.valueOf(numberToBuy * price) + ChatColor.GREEN  +
                            " has been subtracted from your account.");
                } catch (UserDoesNotExistException e) {
                    //e.printStackTrace();
                } catch (NoLoanPermittedException e) {
                    //e.printStackTrace();
                }
                player.closeInventory();
            }else{
                player.sendMessage(ChatColor.RED + "You do not have enough money to buy that.");
            }
        }
    }

    private void incrementCounters(boolean direction){
        if (direction){
            numberToBuy++;
            if (numberToBuy > 64){
                numberToBuy = 64;
            }
        }else{
            numberToBuy--;
            if (numberToBuy < 1){
                numberToBuy = 1;
            }
        }
    }

    private boolean canBuy(Player player){
        try {
            return Economy.hasEnough(player.getName(), BigDecimal.valueOf(numberToBuy * price));
        } catch (UserDoesNotExistException e) {
            return false;
        }
    }
}

