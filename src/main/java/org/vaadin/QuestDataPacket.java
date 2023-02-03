package org.vaadin;

import org.vaadin.card.Card;
import org.vaadin.card.QuestCard;

import java.util.ArrayList;

public class QuestDataPacket extends StoryDataPacket {
    /*
    questPhase:
    -1 : not initialized
     0 : not used
     1 : Choosing sponsor - what was potentialSponsorID is now activePlayerID
     2 : Sponsor choosing cards - sponsorID should == activePlayerID
     4 : Other players choosing to go on the quest
     5 : Questing players playing weapons (or amours + allies) face down
<<<<<<< HEAD

     NEW for Tests:
=======
         NEW for Tests:
>>>>>>> 9cdc093 (test cards)
     6 : Asking player if they want to up the bid
        - respond with QuestStageMediator.submitBidDecision(boolean decision)
     7 : Top bidder discarding
        - respond with QuestStageMediator.submitTestDiscard() to signify when done
     */




    int questPhase = -1;

    int activePlayerID = -1;
    int sponsorID = -1;
    int stageCount = -1;
    int currentStage = -1;
    int currentBid = -1;
    int testDiscardCount = 0;

    ArrayList<ArrayList<Card>> revealedChallengeCards = new ArrayList<>();

    public QuestDataPacket(QuestCard c){
        stageCount = c.getStages();
        for(int i = 0; i<stageCount; i++){
            revealedChallengeCards.add(new ArrayList<>());
        }
    }
    public int getSponsorId(){
        return sponsorID;
    }
    public int getStageCount(){
        return stageCount;
    }

}
