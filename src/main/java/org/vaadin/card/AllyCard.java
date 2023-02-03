package org.vaadin.card;

public class AllyCard extends Card {

    AllyInfo info;

    public AllyCard(String n, AllyInfo ai) {
        type = "Ally";
        name = n;
        info = ai;
        path = info.getPathToImage();;

    }

    public int getHP(){
        return info.getHP();
    }

    public String toString() {
        return ("Name: " + name + ". Type: " + type + " Card. " + info.toString());
    }

}
