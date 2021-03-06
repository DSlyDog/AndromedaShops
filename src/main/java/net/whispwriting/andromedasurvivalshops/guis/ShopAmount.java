package net.whispwriting.andromedasurvivalshops.guis;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import net.ess3.api.Economy;
import net.whispwriting.andromedasurvivalshops.AylaShops;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public class ShopAmount {

    private Inventory inv;
    private String inventoryString;
    private int columns = 9;
    private int rows = columns * 6;
    private String name;
    private Material material;
    private int numberToBuy = 1;
    private double price, sellPrice;
    private ItemStack buying, buy, up, down, sell;

    public ShopAmount(Material material, String name, double price, double sellPrice){
        this.name = name;
        this.material = material;
        this.price = price;
        this.sellPrice = sellPrice;
    }

    public void init(){
        inventoryString = Utils.chat(name);
        inv = Bukkit.createInventory(null, rows, inventoryString);
        buying = Utils.createItem(material, price, sellPrice);
        buy = Utils.createItem(Material.BLUE_WOOL, "Purchase", "purchase");
        up = Utils.createItem(Material.GREEN_WOOL, "Increase amount to buy", "increase");
        down = Utils.createItem(Material.RED_WOOL, "Decrease amount to buy", "decrease");
        sell = Utils.createItem(Material.LIGHT_BLUE_WOOL, "Sell", "sell");
        inv.setItem(13, buying);
        inv.setItem(29, down);
        inv.setItem(31, buy);
        inv.setItem(33, up);
        inv.setItem(49, sell);
    }

    public Inventory GUI(){
        return inv;
    }

    public void clicked(Player player, ItemStack clicked, AylaShops plugin){
        String id = clicked.getItemMeta().getLocalizedName();
        if (id.equals("increase")){
            incrementCounters(true);
            buying.setAmount(numberToBuy);
            inv.setItem(13, buying);
            inv.setContents(inv.getContents());
        }else if (id.equals("decrease")){
            incrementCounters(false);
            buying.setAmount(numberToBuy);
            inv.setItem(13, buying);
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
        }else if (id.equals("sell")){
            ItemStack[] inventory = player.getInventory().getContents();
            for (ItemStack item : inventory){
                if (item.getType() == buying.getType()){
                    if (item.getAmount() >= numberToBuy){
                        try {
                            Economy.add(player.getName(), BigDecimal.valueOf(numberToBuy * sellPrice));
                            item.setAmount(item.getAmount() - numberToBuy);
                            player.getInventory().setContents(inventory);
                            player.updateInventory();
                            player.sendMessage(ChatColor.GREEN + "Sale successful. " + ChatColor.RED + "$" + BigDecimal.valueOf(numberToBuy * sellPrice) + ChatColor.GREEN  +
                                    " has been added to your account.");
                        }catch(UserDoesNotExistException e){
                            e.printStackTrace();
                        }catch(NoLoanPermittedException e){
                            e.printStackTrace();
                        }
                    }else{
                        player.sendMessage(ChatColor.RED + "You don't have enough of that item in your inventory to sell.");
                    }
                }
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

