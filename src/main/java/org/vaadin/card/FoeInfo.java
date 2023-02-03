package org.vaadin.card;

public class FoeInfo {
    private int lowHP;
    private int highHP;
    private String special;
    private String pathToImage;

    public FoeInfo(int l, int h, String s, String p) {
        lowHP = l;
        highHP = h;
        special = s;
        pathToImage = p;
    }

    public int getLowHP() { return lowHP; }

    public int getHighHP() { return highHP; }

    public String getSpecial() { return special; }

    public String getPathToImage() { return pathToImage; }

    public String toString() {
        return ("Low HP: " + lowHP + ". High HP: " + highHP + ". Special: " + special + ".");
    }
}