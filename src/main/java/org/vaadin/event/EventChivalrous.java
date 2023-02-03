package org.vaadin.event;

import org.vaadin.Mediator;
import org.vaadin.Player;

import java.util.ArrayList;

public class EventChivalrous extends EventStoryParent implements EventStory   {


    public EventChivalrous(Mediator med) {
        super(med);
    }


    @Override
    public void event() {
        //Player(s) with both lowest rank and least amount of shields, receive 3 shields

        Player temp = null;
        ArrayList<Player> lowestRanked = new ArrayList<>();

        for(int i=0; i<mediator.getPlayerCount(); i++){
            //System.out.println(mediator.getPlayer(i).getShields());
            if(temp == null){
                temp = mediator.getPlayer(i);
                lowestRanked.add(mediator.getPlayer(i));
            }
            else if(temp.totalPlayerShields() <= mediator.getPlayer(i).totalPlayerShields() ){
                temp = mediator.getPlayer(i);
                if(temp.totalPlayerShields() < lowestRanked.get(0).totalPlayerShields()){
                    lowestRanked.clear();
                    lowestRanked.add(mediator.getPlayer(i));
                }else if(temp.totalPlayerShields() == lowestRanked.get(0).totalPlayerShields()){
                    lowestRanked.add(mediator.getPlayer(i));
                }

            }
        }

        for(int i=0; i< lowestRanked.size(); i++){
            System.out.println(lowestRanked.get(i).getPlayerID());
            lowestRanked.get(i).setShields(lowestRanked.get(i).getShields() + 3);
        }
    }

}
