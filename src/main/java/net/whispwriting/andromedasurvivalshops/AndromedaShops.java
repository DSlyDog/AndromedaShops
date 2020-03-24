package net.whispwriting.andromedasurvivalshops;

import net.whispwriting.andromedasurvivalshops.commands.ShopCommand;
import net.whispwriting.andromedasurvivalshops.files.ConfigFile;
import net.whispwriting.andromedasurvivalshops.guis.Shop;
import net.whispwriting.andromedasurvivalshops.utils.MySQL;
import net.whispwriting.andromedasurvivalshops.utils.SQL;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class AndromedaShops extends JavaPlugin {

    private Map<String, Shop> shops = new HashMap<>();
    private ConfigFile config = new ConfigFile(this);
    private SQL sql;

    @Override
    public void onEnable() {
        config.createDefaults();
        config.get().options().copyDefaults(true);
        config.save();
        boolean useRemote = config.get().getBoolean("remote-database");
        if (useRemote) {
            sql = new MySQL(config, this, shops);
            sql.setup();
            sql.loadShops();
        }
        ShopCommand shopCommand = new ShopCommand(shops, this, sql);
        this.getCommand("shop").setExecutor(shopCommand);
    }

    @Override
    public void onDisable() {
        //
    }
}
