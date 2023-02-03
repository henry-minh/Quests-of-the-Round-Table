package org.vaadin.event;

import org.vaadin.Mediator;

public class EventPlague extends EventStoryParent  implements EventStory {


    public EventPlague(Mediator med) {
        super(med);
    }

    @Override
    public void event() {
        //Drawer loses 2 shields if possible

        mediator.getCurrPlayer().setShields(mediator.getCurrPlayer().getShields() - 2);
    }
}
