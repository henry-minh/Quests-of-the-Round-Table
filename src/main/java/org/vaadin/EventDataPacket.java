package org.vaadin;

import java.util.ArrayList;

public class EventDataPacket extends StoryDataPacket{

     boolean kingCall;
     ArrayList<Player> kingCallHighestRanked;

     public EventDataPacket(boolean kingCall){
         this.kingCall = kingCall;
         kingCallHighestRanked = new ArrayList<>();
     }

     public void setKingCallHighestRanked(ArrayList<Player> arr){
         kingCallHighestRanked = arr;
     }


}
