package org.vaadin.event;

import org.vaadin.Mediator;
import org.vaadin.Player;

import java.util.ArrayList;

public class EventQueenFavor extends EventStoryParent implements EventStory {


    public EventQueenFavor(Mediator med) {
        super(med);
    }

    @Override
    public void event() {
        //The lowest rank player(s) immediately receives 2 adventure cards

        Player temp = null;
        ArrayList<Player> lowestRanked = new ArrayList<>();

        for(int i=0; i<mediator.getPlayerCount(); i++){
            //System.out.println(mediator.getPlayer(i).getShields());
            if(temp == null){
                temp = mediator.getPlayer(i);
                lowestRanked.add(mediator.getPlayer(i));
            }
            else if(temp.getRank().getReqShields() <= mediator.getPlayer(i).getRank().getReqShields() ){
                temp = mediator.getPlayer(i);
                if(temp.getRank().getReqShields() < lowestRanked.get(0).getRank().getReqShields()){
                    lowestRanked.clear();
                    lowestRanked.add(mediator.getPlayer(i));
                }else if(temp.getRank().getReqShields() == lowestRanked.get(0).getRank().getReqShields()){
                    lowestRanked.add(mediator.getPlayer(i));
                }

            }
        }
        System.out.println(lowestRanked);
        for(int i=0; i< lowestRanked.size(); i++){
            System.out.println(lowestRanked.get(i).getPlayerID());
            lowestRanked.get(i).drawAdvCards(2);
        }
    }
}
