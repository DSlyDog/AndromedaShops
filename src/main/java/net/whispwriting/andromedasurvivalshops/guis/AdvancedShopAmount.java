package net.whispwriting.andromedasurvivalshops.guis;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import net.ess3.api.Economy;
import net.whispwriting.andromedasurvivalshops.AylaShops;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AdvancedShopAmount {

    private UIItemData items;
    private Inventory inv;
    private String inventoryString;
    private int columns = 9;
    private int rows = columns * 6;
    private String name;
    private Material material;
    private int numberToBuy = 1;
    private double price, sellPrice;
    private ItemStack buying, buy;

    public AdvancedShopAmount(Material material, String name, double price, double sellPrice, UIItemData items){
        this.name = name;
        this.material = material;
        this.price = price;
        this.sellPrice = sellPrice;
        this.items = items;
    }

    public void init(){
        inventoryString = Utils.chat(name);
        inv = Bukkit.createInventory(null, rows, inventoryString);
        buying = Utils.createItem(material, price, sellPrice);
        buy = Utils.createItem(Material.BLUE_WOOL, "Purchase", "purchase");
        inv.setItem(13, buying);
        inv.setItem(31, buy);
    }

    public Inventory GUI(){
        return inv;
    }

    public void clicked(Player player, ItemStack clicked, AylaShops plugin){
        String id = clicked.getItemMeta().getLocalizedName();
        if (id.equals("purchase")){
            if (canBuy(player)) {
                try {
                    for (String command : items.getCommands()){
                        CommandSender sender = Bukkit.getConsoleSender();
                        command = command.replace("@p", player.getName());
                        Bukkit.dispatchCommand(sender, command);
                    }
                    Economy.substract(player.getName(), BigDecimal.valueOf(numberToBuy * price));
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
            if (Economy.playerExists(player.getName()))
                return Economy.hasEnough(player.getName(), BigDecimal.valueOf(numberToBuy * price));
            return false;
        } catch (UserDoesNotExistException e) {
            return false;
        }
    }
}
