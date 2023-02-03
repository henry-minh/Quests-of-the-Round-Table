package org.vaadin.event;

import org.vaadin.Mediator;

public class EventKingRec extends EventStoryParent implements EventStory {


    public EventKingRec(Mediator med) {
        super(med);
    }


    @Override
    public void event() {
        //The next player(s) to complete a Quest will receive 2 extra shields

        //have a var in mediator Bool questKingRec and set it to true
        //when done the quest set it to false
        mediator.setKingRec(true);
    }
}
