package org.vaadin.event;

import org.vaadin.Mediator;
import org.vaadin.Player;

public class EventPox extends EventStoryParent implements EventStory {

    public EventPox(Mediator med) {
        super(med);
    }

    @Override
    public void event() {
        //all players except the player drawing this card lose 1 shield

        for(int i=0; i<mediator.getPlayerCount();i++){
            if(mediator.getPlayer(i).getPlayerID() != mediator.getCurrPlayer().getPlayerID()){
                mediator.getPlayer(i).setShields(mediator.getPlayer(i).getShields() - 1);
            }
        }
    }
}
