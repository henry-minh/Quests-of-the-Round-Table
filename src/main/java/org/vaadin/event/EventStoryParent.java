package org.vaadin.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.Mediator;

public abstract class EventStoryParent {
    Mediator mediator;

    public EventStoryParent(Mediator med){
        mediator = med;
    }
}
