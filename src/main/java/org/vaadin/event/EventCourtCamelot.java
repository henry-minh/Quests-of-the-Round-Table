package org.vaadin.event;

import org.vaadin.GameDataPacket;
import org.vaadin.Mediator;
import org.vaadin.Player;

public class EventCourtCamelot extends EventStoryParent implements EventStory {

    GameDataPacket gameDataPacket;

    public EventCourtCamelot(Mediator med) {
        super(med);
    }

    @Override
    public void event() {
        //all allies in play must be discarded


        /*for(int i=0; i<mediator.getPlayerCount(); i++){
            for(int j=0; j< mediator.getPlayer(i).getAllies().size(); j++){

                //add to discard
                //remove allie from player
            }
        }*/

    }
}
