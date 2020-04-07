package net.whispwriting.andromedasurvivalshops.utils;

import net.whispwriting.andromedasurvivalshops.AylaShops;
import net.whispwriting.andromedasurvivalshops.guis.UIItemData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public abstract class SQL {

    protected Connection connection;
    protected AylaShops plugin;

    public SQL(AylaShops plugin){
        this.plugin = plugin;
    }

    abstract void connect();

    public abstract void setup();
    public abstract void loadShops();
    public abstract boolean addShop(String name, String colors);
    public abstract boolean addShopAdvanced(String name, String colors);
    public abstract boolean createShop(String name);
    public abstract boolean createShopAdvanced(String name);
    public abstract ResultSet loadShop(String name);
    public abstract void addItem(UIItemData item, String name);
    public abstract void addItemAdvanced(UIItemData item, String name);
    public abstract boolean deleteShop(String name);
    public abstract boolean deleteShopAdvanced(String name);
    public abstract void deleteItem(String shop, UIItemData item);

    protected boolean tableExists(String table){
        try {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, table, null);
            if (tables.next()){
                return true;
            }else{
                return false;
            }
        }catch(SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to verify the existence of " + table + ". Loading data from it will fail.");
            e.printStackTrace();
            return true;
        }
    }

    protected void createShopList(String name){
        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE " + name + " (name VARCHAR(50), colors VARCHAR(50))");
            statement.executeUpdate();
        }catch(SQLException e){
            plugin.getLogger().log(Level.WARNING, "Failed to create the shops list.");
            e.printStackTrace();
        }
    }

    protected String itemStackToBase64(ItemStack item) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeObject(item);
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }

    public void fixIndex(String shop){
        connect();
        try {
            PreparedStatement statement = connection.prepareStatement("select * from " + shop);
            ResultSet results = statement.executeQuery();
            int i = 0;
            int page = 1;
            while (results.next()){
                statement = connection.prepareStatement("update " + shop + " set indexs=" + i + ", page=" + page + " where id='" + results.getString("id") + "'");
                statement.executeUpdate();
                i++;
                if (i > 44){
                    i = 0;
                    page++;
                }
            }
        }catch(SQLException e){
            plugin.getLogger().log(Level.WARNING, "Failed to create the shops list.");
            e.printStackTrace();
        }
    }
}
