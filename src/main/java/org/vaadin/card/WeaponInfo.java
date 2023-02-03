package org.vaadin.card;

public class WeaponInfo {
    int damage;
    String path;

    WeaponInfo(int b, String p) {
        damage = b;
        path = p;
    }

    public int getDamage() {
        return damage;
    }

    public String getPath() {
        return path;
    }
}