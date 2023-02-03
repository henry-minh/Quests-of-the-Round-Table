package org.vaadin.card;

import java.util.ArrayList;

public class QuestInfo {
    private int stages;
    private ArrayList<String> foes;
    private String pathToImage;

    public QuestInfo(int s, ArrayList<String> f, String p) {
        stages = s;
        foes = f;
        pathToImage = p;
    }

    public ArrayList<String> getFoes() {
        return foes;
    }

    public int getStages() {
        return stages;
    }

    public String getPathToImage() { return pathToImage; }

    public String toString() {
        return ("Stages: " + stages + ". Foe: " + foes + ".");
    }
}
