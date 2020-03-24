package net.whispwriting.andromedasurvivalshops.files;

import net.whispwriting.andromedasurvivalshops.AndromedaShops;

public class ConfigFile extends AbstractFile {

    public ConfigFile(AndromedaShops pl) {
        super(pl, "config.yml", "");
    }

    public void createDefaults(){
        config.addDefault("remote-database", false);
        config.addDefault("host", "localhost");
        config.addDefault("port", 3306);
        config.addDefault("username", "username");
        config.addDefault("password", "password");
        config.addDefault("database", "root");
    }
}
