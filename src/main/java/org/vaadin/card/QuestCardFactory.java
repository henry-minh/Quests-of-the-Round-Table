package org.vaadin.card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QuestCardFactory implements CardFactory {

    private final ArrayList<String> allFoes = new ArrayList<>(Arrays.asList("Robber Knight", "Saxons", "Boar", "Green Knight", "Black Knight", "Evil Knight",
            "Dragon", "Giant", "Mordred", "Saxon Knight", "Thieves"));

    private Map<String, QuestInfo> questMap = new HashMap() {{
        put("Journey through the Enchanted Forest", new QuestInfo(3, new ArrayList<String>(Arrays.asList("Evil Knight")), "QuestCardsImages/Quest/Quest_Quest_EnchantedForest.png"));
        put("Vanquish King Arthur's Enemies", new QuestInfo(3, new ArrayList<String>(Arrays.asList()), "QuestCardsImages/Quest/Quest_Quest_VanquishKingArthurEnemies.png"));
        put("Repel the Saxon Raiders", new QuestInfo(2, new ArrayList<String>(Arrays.asList("Saxons", "Saxon Knight")), "QuestCardsImages/Quest/Quest_Quest_RepelSaxonRaider.png"));
        put("Boar Hunt", new QuestInfo(2, new ArrayList<String>(Arrays.asList("Boar")), "QuestCardsImages/Quest/Quest_Quest_BoarHunt.png"));
        put("Search for the Questing Beast", new QuestInfo(4, new ArrayList<String>(Arrays.asList()), "QuestCardsImages/Quest/Quest_Quest_SearchForQuestingBeast.png"));
        put("Defend the Queen's Honor", new QuestInfo(4, allFoes, "QuestCardsImages/Quest/Quest_Quest_DefendQueenHonor.png"));
        put("Slay the Dragon", new QuestInfo(3, new ArrayList<String>(Arrays.asList("Dragon")), "QuestCardsImages/Quest/Quest_Quest_SlayTheDragon.png"));
        put("Rescue the Fair Maiden", new QuestInfo(3, new ArrayList<String>(Arrays.asList("Black Knight")), "QuestCardsImages/Quest/Quest_Quest_RescueFairMaiden.png"));
        put("Search for the Holy Grail", new QuestInfo(5, allFoes, "QuestCardsImages/Quest/Quest_Quest_SearchForHolyGrail.png"));
        put("Test of the Green Knight", new QuestInfo(4, new ArrayList<String>(Arrays.asList("Green Knight")), "QuestCardsImages/Quest/Quest_Quest_TestOfGreenKnight.png"));

    }};

    public Card createCard(String n) {
        return new QuestCard(n, questMap.get(n));
    }
}