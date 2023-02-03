package org.vaadin.card;

public class TestCard extends Card {

    //private TestInfo info;
    private int minBid;

    public TestCard(String n, TestInfo ti) {
        type = "Test";
        name = n;
        minBid = ti.getBids();
        path = ti.getPath();
    }

    public String toString() {
        return ("Name: " + name + ". Type: " + type + " Card. Min Bids: " + minBid + ".");
    }

    public int getMinBid() { return minBid; }


}
