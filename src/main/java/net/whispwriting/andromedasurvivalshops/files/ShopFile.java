package net.whispwriting.andromedasurvivalshops.files;

import net.whispwriting.andromedasurvivalshops.AylaShops;

public class ShopFile extends AbstractFile {

    public ShopFile(AylaShops pl, String filename) {
        super(pl, filename + ".yml", "/shops");
    }
}
