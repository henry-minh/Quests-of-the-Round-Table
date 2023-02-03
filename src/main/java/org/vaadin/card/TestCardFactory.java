package org.vaadin.card;

import java.util.HashMap;
import java.util.Map;

public class TestCardFactory implements CardFactory {

    private Map<String, TestInfo> testMap = new HashMap() {{
        put("Test of the Questing Beast", new TestInfo(4, "QuestCardsImages/Test/Quest_Test_TestOfQuestingBeast.png"));
        put("Test of Temptation", new TestInfo(0, "QuestCardsImages/Test/Quest_Test_TestOfTemptation.png"));
        put("Test of Valor", new TestInfo(0, "QuestCardsImages/Test/Quest_Test_TestOfValor.png"));
        put("Test of Morgan Le Fey", new TestInfo(3, "QuestCardsImages/Test/Quest_Test_TestOfMorganLeFey.png"));
    }};

    public Card createCard(String n) {
        return new TestCard(n, testMap.get(n));
    }
}