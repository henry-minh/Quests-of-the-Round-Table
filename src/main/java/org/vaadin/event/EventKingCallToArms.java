package org.vaadin.event;

import org.vaadin.*;

import java.util.ArrayList;

public class EventKingCallToArms extends EventStoryParent implements EventStory {

    public EventKingCallToArms(Mediator med) {
        super(med);
    }

    @Override
    public void event() {
        //The highest ranked player(s) must place 1 weapon in the discard pile. If unable to do so, 2 Foe Cards must be discarded


        Player temp = null;
        ArrayList<Player> highestRanked = new ArrayList<>();

        for(int i=0; i<mediator.getPlayerCount(); i++){
            if(temp == null){
                temp = mediator.getPlayer(i);
                highestRanked.add(mediator.getPlayer(i));
            }
            else if(temp.getRank().getReqShields() >= mediator.getPlayer(i).getRank().getReqShields() ){
                temp = mediator.getPlayer(i);
                if(temp.getRank().getReqShields() > highestRanked.get(0).getRank().getReqShields()){
                    highestRanked.clear();
                    highestRanked.add(mediator.getPlayer(i));
                }else if(temp.getRank().getReqShields() == highestRanked.get(0).getRank().getReqShields()){
                    highestRanked.add(mediator.getPlayer(i));
                }
            }
        }

        EventDataPacket eventDataPacket;
        if (mediator.getStoryDataPacket() instanceof EventDataPacket) {
            eventDataPacket = (EventDataPacket) mediator.getStoryDataPacket();
            eventDataPacket.setKingCallHighestRanked(highestRanked);
        } else {
            System.out.println("MainView: Attempt to load quest when no quest available");
            return;
        }

        //System.out.println(highestRanked);
        //Will need UI update for the highest ranked player
        //They pick a weapon to discard, if they cant 2 foe
    }
}
