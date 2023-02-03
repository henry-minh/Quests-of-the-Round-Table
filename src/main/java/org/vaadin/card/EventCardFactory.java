package org.vaadin.card;

import java.util.HashMap;
import java.util.Map;

public class EventCardFactory implements CardFactory {
    Map<String, String> eventMap = new HashMap(){{
        put("King's Call to Arms","QuestCardsImages/Events/Quest_Event_KinCall.png");
        put("Court Called to Camelot","QuestCardsImages/Events/Quest_Event_CourtCamelot.png");
        put("King's Recognition","QuestCardsImages/Events/Quest_Event_KinRec.png");
        put("Plague","QuestCardsImages/Events/Quest_Event_Plague.png");
        put("Prosperity Throughout the Realm","QuestCardsImages/Events/Quest_Event_Prosperity.png");
        put("Pox","QuestCardsImages/Events/Quest_Event_Pox.png");
        put("Queen's Favor","QuestCardsImages/Events/Quest_Event_QueenFavor.png");
        put("Chivalrous Deed","QuestCardsImages/Events/Quest_Event_Chivalrous.png");

    }};
    
    public Card createCard(String n) {
        return new EventCard(n, eventMap.get(n));
    }
}
