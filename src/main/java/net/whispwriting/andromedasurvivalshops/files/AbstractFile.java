package net.whispwriting.andromedasurvivalshops.files;

import net.whispwriting.andromedasurvivalshops.AylaShops;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

abstract class AbstractFile {

    private AylaShops plugin;
    private File file;
    protected FileConfiguration config;
    private File dir;
    private String filename;

    public AbstractFile(AylaShops pl, String filename, String d){
        plugin = pl;
        this.filename = filename;
        File dir = new File(pl.getDataFolder() + d);
        if (!dir.exists()){
            dir.mkdirs();
        }
        this.dir = dir;
        file = new File(dir, filename);
        if (!file.exists()){
            try{
                file.createNewFile();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }
    public void save(){
        try{
            config.save(file);
        }catch(IOException e){
            System.out.println("Could not save file");
        }
    }
    public FileConfiguration get(){
        return config;
    }

    public void reload(){
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void delete(){
        config = null;
        file.delete();
    }
}

