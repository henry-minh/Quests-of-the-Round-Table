package org.vaadin.card;

import java.util.ArrayList;

public class QuestCard extends Card {

    private QuestInfo info;
    public QuestCard(String n, QuestInfo qi) {
        type = "Quest";
        name = n;
        info = qi;
        path = info.getPathToImage();
    }

    public int getStages(){
        return info.getStages();
    }

    public String toString() {
        return ("Name: " + name + ". Type: " + type + " Card. " + info.toString());
    }

    public ArrayList<String> getFoes() { return info.getFoes(); }
}
