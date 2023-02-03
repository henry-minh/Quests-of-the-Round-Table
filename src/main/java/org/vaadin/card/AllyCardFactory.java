package org.vaadin.card;

import java.util.HashMap;
import java.util.Map;

public class AllyCardFactory implements CardFactory {
    private Map<String, AllyInfo> allyMap = new HashMap() {{
        put("Sir Gawain", new AllyInfo(10, 0, "+20 HP on the Test of the Green Knight Quest", "QuestCardsImages/Special/Quest_Special_SirGawain.png"));
        put("King Pellinore", new AllyInfo(10, 0, "+4 bids on the Search for the Questing Beast Quest", "QuestCardsImages/Special/Quest_Special_KingPellinore.png"));
        put("Sir Percival", new AllyInfo(5, 0, "+20 on the Search for the Holy Grail Quest", "QuestCardsImages/Special/Quest_Special_SirPercival.png"));
        put("Sir Tristan", new AllyInfo(10, 0, "+20 when Queen Iseult is in play", "QuestCardsImages/Special/Quest_Special_SirTristan.png"));
        put("King Arthur", new AllyInfo(10, 2, "N/A", "QuestCardsImages/Special/Quest_Special_KingArthur.png"));
        put("Queen Guinevere", new AllyInfo(0, 3, "N/A", "QuestCardsImages/Special/Quest_Special_QueenGuinevere.png"));
        put("Merlin", new AllyInfo(0, 0, "Player may preview any one stage per quest", "QuestCardsImages/Special/Quest_Special_Merlin.png"));
        put("Queen Iseult", new AllyInfo(0, 2, "+2 bids when Sir Tristan is in play", "QuestCardsImages/Special/Quest_Special_QueenIseult.png"));
        put("Sir Lancelot", new AllyInfo(15, 0, "+25 when on the Quest to Defend the Queen's Honor", "QuestCardsImages/Special/Quest_Special_SirLancelot.png"));
        put("Sir Galahad", new AllyInfo(15, 0, "N/A", "QuestCardsImages/Special/Quest_Special_SirGalahad.png"));
    }};

    public Card createCard(String n) {
        return new AllyCard(n, allyMap.get(n));
    }
}
