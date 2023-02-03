package org.vaadin.card;

public class AllyInfo {
    private int HP;
    private int bids;
    private String special;
    private String pathToImage;

    public AllyInfo(int h, int b, String s, String p) {
        HP = h;
        bids = b;
        special = s;
        pathToImage = p;
    }

    public int getHP() { return HP; }

    public int getBids() { return bids; }

    public String getSpecial() { return special; }

    public String getPathToImage() { return pathToImage; }

    public String toString() {
        return ("HP: " + HP + ". Bids: " + bids + ". Special: " + special + ".");
    }
}
