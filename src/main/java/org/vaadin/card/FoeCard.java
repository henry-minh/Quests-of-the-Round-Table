package org.vaadin.card;

public class FoeCard extends Card {

    private FoeInfo info;

    public FoeCard(String n, FoeInfo fi) {
        type = "Foe";
        name = n;
        info = fi;
        path = info.getPathToImage();

    }

    public String toString() {
        return ("Name: " + name + ". Type: " + type + " Card. " + info.toString());
    }

    public int getLowHP() { return info.getLowHP(); }

    public int getHighHP() { return info.getHighHP(); }

    public String getSpecial() { return info.getSpecial(); }

}
