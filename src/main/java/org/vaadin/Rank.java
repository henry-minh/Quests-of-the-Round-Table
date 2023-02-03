package org.vaadin;

public class Rank {
    private String name;
    private int reqShields;
    private int baseBP;

    public Rank(String name, int reqShields, int baseBP) {
        this.name = name;
        this.reqShields = reqShields;
        this.baseBP = baseBP;
    }

    public int getBP() {
        return baseBP;
    }

    public int getReqShields() {
        return reqShields;
    }

    public String getName() {
        return name;
    }
}
