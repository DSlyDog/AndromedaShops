package net.whispwriting.andromedasurvivalshops.guis;

import net.whispwriting.andromedasurvivalshops.AylaShops;
import net.whispwriting.andromedasurvivalshops.events.ShopAmountInteract;
import net.whispwriting.andromedasurvivalshops.events.ShopAmountInteractAdvanced;
import net.whispwriting.andromedasurvivalshops.utils.SQL;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvancedShop {
    private List<UIItemData> items = new ArrayList<>();
    private Inventory inv, returnInv;
    private String inventoryString, name, colors;
    private int columns = 9;
    private int rows = columns * 6;
    private int currentIndex = -1;
    private int currentPage = 1;
    private int onPage = 1;
    private SQL sql;

    public AdvancedShop(String name, String colors, SQL sql){
        this.name = name;
        this.colors = colors;
        this.sql = sql;
        if(sql.createShopAdvanced(name))
            sql.addShopAdvanced(name, colors);
    }

    public AdvancedShop(String name, String colors, List<UIItemData> items){
        this.name = name;
        this.items = items;
        this.colors = colors;
    }

    public void init(){
        inventoryString = Utils.chat(colors + name);
        inv = Bukkit.createInventory(null, rows, inventoryString);
        onPage = 1;
        buildPage();
        inv.setContents(returnInv.getContents());
    }

    public Inventory GUI(){
        return inv;
    }

    public boolean addItem(String type, String id, double price, double sellPrice, List<String> commands, List<String> lore){
        incrementCounters();
        UIItemData item = Utils.createItem(type, id, price, sellPrice, currentPage, currentIndex, items, commands, lore);
        if (item != null)
            sql.addItemAdvanced(item, name);
        return true;
    }

    private void incrementCounters() {
        currentIndex++;
        if (currentIndex > 44){
            currentIndex = 0;
            currentPage++;
        }
    }

    public String getName(){
        return name;
    }

    public String getColors(){
        return colors;
    }

    public void clicked(Player player, ItemStack clicked, AylaShops plugin){
        String id = clicked.getItemMeta().getLocalizedName();
        if (id.equals("nextPage")){
            incrementPage(true);
            if (buildPage()) {
                inv.setContents(returnInv.getContents());
            }
        }else if (id.equals("previousPage")){
            incrementPage(false);
            if (buildPage()) {
                inv.setContents(returnInv.getContents());
            }
        }else{
            for (UIItemData itemData : items) {
                if (itemData.getID().equals(id)) {
                    AdvancedShopAmount shopAmount = new AdvancedShopAmount(clicked.getType(), clicked.getType().name(), itemData.getPrice(), itemData.getSellPrice(), itemData);
                    shopAmount.init();
                    player.closeInventory();
                    Bukkit.getPluginManager().registerEvents(new ShopAmountInteractAdvanced(plugin, player.getUniqueId().toString(), shopAmount), plugin);
                    player.openInventory(shopAmount.GUI());
                    break;
                }
            }
        }
    }

    private void incrementPage(boolean direction){
        if (direction){
            onPage++;
        }else{
            onPage--;
            if (onPage < 1){
                onPage = 1;
            }
        }
    }

    private boolean buildPage(){
        int itemsAdded = 0;
        inventoryString = Utils.chat(colors + name);
        returnInv = Bukkit.createInventory(null, rows, inventoryString);
        for (UIItemData itemData : items){
            if (itemData.isOnPage(onPage)){
                returnInv.setItem(itemData.getIndex(), itemData.getItem());
                itemsAdded++;
            }
        }
        ItemStack previous = Utils.createItem(Material.RED_WOOL, "Previous Page", "previousPage");
        ItemStack next = Utils.createItem(Material.GREEN_WOOL, "Next Page", "nextPage");
        ItemStack pageNumber = Utils.createItem(Material.PAPER, "Current Page: " + onPage, "pageNum");
        pageNumber.setAmount(onPage);
        returnInv.setItem(45, previous);
        returnInv.setItem(49, pageNumber);
        returnInv.setItem(53, next);
        if (itemsAdded == 0){
            onPage--;
            return false;
        }
        return true;
    }

    public UIItemData getItem(String id) {
        for (UIItemData itemData : items){
            if (itemData.getID().equals(id))
                return itemData;
        }
        return null;
    }

    public List<UIItemData> getItems(){
        return items;
    }

    public boolean load(){
        ResultSet itemSet = sql.loadShop(name);
        try {
            while (itemSet.next()) {
                double price = itemSet.getDouble("buy");
                double sellPrice = itemSet.getDouble("sell");
                int page = itemSet.getInt("page");
                int index = itemSet.getInt("indexs");
                String id = itemSet.getString("id");
                String commands = itemSet.getString("commands");
                String info = itemSet.getString("info");
                ItemStack item = itemStackFromBase64(itemSet.getString("item"));
                String[] infoBlock = info.split(",");
                String[] cmdBlock = commands.split(",");
                List<String> infoArray = new ArrayList<>(Arrays.asList(infoBlock));
                List<String> cmdArray = new ArrayList<>(Arrays.asList(cmdBlock));
                UIItemData itemData = new UIItemData(item, id, price, sellPrice, page, index, cmdArray, infoArray);
                items.add(itemData);
            }
            if (!items.isEmpty()) {
                currentIndex = items.get(items.size() - 1).getIndex();
                currentPage = items.get(items.size() - 1).getPage();
            }
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(){
        return sql.deleteShopAdvanced(name);
    }

    public ItemStack itemStackFromBase64(String data) {
        try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(new ByteArrayInputStream(Base64Coder.decodeLines(data)));) {

            // Read the serialized item
            ItemStack item = (ItemStack) dataInput.readObject();

            return item;
        } catch (IOException | ClassNotFoundException ex) {
            return null;
        }
    }
}
