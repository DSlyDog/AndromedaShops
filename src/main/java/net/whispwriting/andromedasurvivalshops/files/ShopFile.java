package net.whispwriting.andromedasurvivalshops.files;

import net.whispwriting.andromedasurvivalshops.AndromedaShops;

public class ShopFile extends AbstractFile {

    public ShopFile(AndromedaShops pl, String filename) {
        super(pl, filename + ".yml", "/shops");
    }
}
