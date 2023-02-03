package org.vaadin.event;

import org.vaadin.Mediator;

public class EventProsperity extends EventStoryParent implements EventStory {


    public EventProsperity(Mediator med) {
        super(med);
    }

    @Override
    public void event() {
        //All players may immediately draw 2 adventure cards

        for(int i = 0; i < mediator.getPlayerCount();i++){
            mediator.getPlayer(i).drawAdvCards(2);
        }
    }
}
