package org.vaadin.card;

import java.util.HashMap;
import java.util.Map;

public class WeaponCard extends Card {
    private int damage;


    public WeaponCard(String n, WeaponInfo wi) {
        type = "Weapon";
        name = n;
        damage = wi.getDamage();
        path = wi.getPath();
    }

    public String toString() {
        return ("Name: " + name + ". Type: " + type + " Card. Damage: " + damage + ".");
    }

    public int getDamage() { return damage; }

}
