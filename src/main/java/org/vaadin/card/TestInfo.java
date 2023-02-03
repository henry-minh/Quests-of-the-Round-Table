package org.vaadin.card;

public class TestInfo {
    int bids;
    String path;

    TestInfo(int b, String p) {
       bids = b;
       path = p;
    }

    public int getBids() {
        return bids;
    }

    public String getPath() {
        return path;
    }
}
