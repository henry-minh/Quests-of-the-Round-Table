package org.vaadin.card;

import java.util.HashMap;
import java.util.Map;

public class WeaponCardFactory implements CardFactory {

    private Map<String, WeaponInfo> weaponMap = new HashMap() {{
        put("Horse", new WeaponInfo(10, "QuestCardsImages/Weapons/Quest_Weapon_Horse.png"));
        put("Sword", new WeaponInfo(10, "QuestCardsImages/Weapons/Quest_Weapon_Sword.png"));
        put("Dagger", new WeaponInfo(5, "QuestCardsImages/Weapons/Quest_Weapon_Dagger.png"));
        put("Excalibur", new WeaponInfo(30, "QuestCardsImages/Weapons/Quest_Weapon_Excalibur.png"));
        put("Lance", new WeaponInfo(20, "QuestCardsImages/Weapons/Quest_Weapon_Lance.png"));
        put("Battle-ax", new WeaponInfo(15, "QuestCardsImages/Weapons/Quest_Weapon_BattleAx.png"));
    }};

    public Card createCard(String n) {
        return new WeaponCard(n, weaponMap.get(n));
    }
}