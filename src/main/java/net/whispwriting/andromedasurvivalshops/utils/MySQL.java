package net.whispwriting.andromedasurvivalshops.utils;

import net.whispwriting.andromedasurvivalshops.AndromedaShops;
import net.whispwriting.andromedasurvivalshops.files.ConfigFile;
import net.whispwriting.andromedasurvivalshops.guis.Shop;
import net.whispwriting.andromedasurvivalshops.guis.UIItemData;

import java.sql.*;
import java.util.Map;
import java.util.logging.Level;

public class MySQL extends SQL{

    private String host, database, username, password;
    private int port;
    private Map<String, Shop> shops;

    public MySQL(ConfigFile config, AndromedaShops plugin, Map<String, Shop> shops){
        super(plugin);
        this.host = config.get().getString("host");
        this.username = config.get().getString("username");
        this.password = config.get().getString("password");
        this.database = config.get().getString("database");
        this.port = config.get().getInt("port");
        this.shops = shops;
    }

    @Override
    public void setup(){
        try{
            synchronized (this){
                if (connection != null && !connection.isClosed()){
                    return;
                }
                connect();
            }
            if(!tableExists("shops")){
                createShopList("shops");
            }
        }catch(SQLException e){
            plugin.getLogger().log(Level.SEVERE, "Failed to make connection to database. Please check your config.");
            e.printStackTrace();
        }
    }

    @Override
    void connect(){
        try{
            synchronized (this){
                if (connection == null || connection.isClosed()) {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                    plugin.getLogger().log(Level.INFO, "Successfully connected to database.");
                }
            }
        }catch(SQLException e){
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to database. Shops will not save.");
            e.printStackTrace();
        }catch (ClassNotFoundException f){
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to database. Shops will not save.");
            f.printStackTrace();
        }
    }

    @Override
    public void loadShops(){
        connect();
        try{
            PreparedStatement statement = connection.prepareStatement("select * from shops");
            ResultSet results = statement.executeQuery();
            while(results.next()){
                String name = results.getString("name");
                String colors = results.getString("colors");
                Shop shop = new Shop(name, colors, this);
                boolean loaded = shop.load();
                if (loaded)
                    shops.put(name, shop);
                else
                    plugin.getLogger().log(Level.WARNING, "Failed to load shop " + name + ".");
            }
        }catch(SQLException e){
            plugin.getLogger().log(Level.SEVERE, "Failed to load shops. Preexisting shops will be unusable; new shops may not save.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean addShop(String name, String colors){
        connect();
        try {
            PreparedStatement statement = connection.prepareStatement("insert into shops values ('" + name + "', '" + colors + "')");
            statement.executeUpdate();
            return true;
        }catch(SQLException e){
            plugin.getLogger().log(Level.SEVERE, "Failed to add " + name + " to the shops list. Items added to this shop will not save.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createShop(String name){
        connect();
        if (!tableExists(name)) {
            try {
                PreparedStatement statement = connection.prepareStatement("CREATE TABLE " + name + " (price double, page int, indexs int, id VARCHAR(50), item VARCHAR(1500))");
                statement.executeUpdate();
                return true;
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to create shop " + name + ". Items added to this shop will not save.");
                e.printStackTrace();
                return false;
            }
        }else{
            return false;
        }
    }

    @Override
    public boolean deleteShop(String name){
        connect();
        try {
            PreparedStatement statement = connection.prepareStatement("delete from shops where name='" + name + "'");
            statement.executeUpdate();
            PreparedStatement statement2 = connection.prepareStatement("DROP TABLE " + name);
            statement2.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to delete shop " + name + ".");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ResultSet loadShop(String name){
        connect();
        try {
            PreparedStatement statement = connection.prepareStatement("select * from " + name);
            return statement.executeQuery();
        }catch(SQLException e){
            plugin.getLogger().log(Level.WARNING, "Failed to retrieve items from shop " + name + ".");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void addItem(UIItemData item, String name) {
        connect();
        if (tableExists(name)) {
            try {
                PreparedStatement statement = connection.prepareStatement("insert into " + name + " values ('" +
                        item.getPrice() + "', '" + item.getPage() + "', '" + item.getIndex() + "', '" + item.getID() + "', '"
                        + itemStackToBase64(item.getItem()) + "')");
                statement.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to add items to shop " + name + ".");
                e.printStackTrace();
            }
        }else{
            plugin.getLogger().log(Level.WARNING, "The shop " + name + " does not exist in the database. The item" +
                    " could not be added.");
        }
    }

    @Override
    public void deleteItem(String shop, UIItemData item){

    }
}
