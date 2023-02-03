package org.vaadin.card;

import java.util.HashMap;
import java.util.Map;

public class FoeCardFactory implements CardFactory {
    
    private Map<String, FoeInfo> foeMap = new HashMap() {{
        put("Robber Knight", new FoeInfo(15, 15, "N/A", "QuestCardsImages/Foe/Quest_Foe_RobberKnight.png"));
        put("Saxons", new FoeInfo(10, 20, "N/A", "QuestCardsImages/Foe/Quest_Foe_Saxons.png"));
        put("Boar", new FoeInfo(5, 15, "N/A", "QuestCardsImages/Foe/Quest_Foe_Boar.png"));
        put("Green Knight", new FoeInfo(25, 40, "N/A", "QuestCardsImages/Foe/Quest_Foe_GreenKnight.png"));
        put("Black Knight", new FoeInfo(25, 35, "N/A", "QuestCardsImages/Foe/Quest_Foe_BlackKnight.png"));
        put("Evil Knight", new FoeInfo(20, 30, "N/A", "QuestCardsImages/Foe/Quest_Foe_EvilKnight.png"));
        put("Dragon", new FoeInfo(50, 70, "N/A", "QuestCardsImages/Foe/Quest_Foe_Dragon.png"));
        put("Giant", new FoeInfo(40, 40, "+N/A", "QuestCardsImages/Foe/Quest_Foe_Giant.png"));
        put("Mordred", new FoeInfo(15, 15, "Use as a Foe or sacrifice at any time to remove any player's Ally from play", "QuestCardsImages/Foe/Quest_Foe_Mordred.png"));
        put("Saxon Knight", new FoeInfo(15, 25, "N/A", "QuestCardsImages/Foe/Quest_Foe_SaxonKnight.png"));
        put("Thieves", new FoeInfo(5, 5, "N/A", "QuestCardsImages/Foe/Quest_Foe_Thieves.png"));
    }};    
    
    public Card createCard(String n) {
        return new FoeCard(n, foeMap.get(n));
    }
}
