package org.vaadin.card;

import java.util.HashMap;
import java.util.Map;

public class TournamentCardFactory implements CardFactory {
    private Map<String,TournamentInfo> tournamentMap = new HashMap(){{
        put("Tournament At Camelot", new TournamentInfo("QuestCardsImages/Tournament/Quest_Tournament_AtCamelot.png",3));
        put("Tournament At Orkney", new TournamentInfo("QuestCardsImages/Tournament/Quest_Tournament_AtOrkney.png",2));
        put("Tournament At Tintagel", new TournamentInfo("QuestCardsImages/Tournament/Quest_Tournament_AtTintagel.png",1));
        put("Tournament At York", new TournamentInfo("QuestCardsImages/Tournament/Quest_Tournament_AtYork.png",0));
    }};

    public Card createCard(String n) {
        return new TournamentCard(n, tournamentMap.get(n));
    }
}
