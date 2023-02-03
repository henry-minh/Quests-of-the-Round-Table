package org.vaadin;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.card.Card;
import org.vaadin.card.FoeCard;
import org.vaadin.card.TestCard;
import org.vaadin.card.WeaponCard;

import java.util.ArrayList;

public class QuestStageMediator {


    private FoeCard foeCard;

    private Card mainCard = null;
    private int currentBid = 0;
    private ArrayList<WeaponCard> foeWeapons = new ArrayList<WeaponCard>();
    private int foePower;
    private ArrayList<Player> questingPlayers;
    private Player activePlayer;
    private QuestDataPacket questDataPacket;
    private int questStageID;

    @Autowired
    private QuestMediator questMediator;

    public QuestStageMediator(int id){
        System.out.println("Quest stage "+id+" created");
        questStageID = id;
    }

    //this is written for foe quest stages, may need to be changed when tests are added.
//    public void start(ArrayList<Player> players){
//        questingPlayers = (ArrayList<Player>) players.clone();
//        activePlayer = questingPlayers.get(0);
//        for(Player player : questingPlayers){
//            player.drawAdventureCard();
//        }
//        questDataPacket.questPhase = 5;
//        requestFaceDownCards(activePlayer);
//    }
    public void start(ArrayList<Player> players){
        questingPlayers = (ArrayList<Player>) players.clone();
        activePlayer = questingPlayers.get(0);
        for(Player player : questingPlayers){
            player.drawAdventureCard();
        }
        if (mainCard instanceof FoeCard){
            questDataPacket.questPhase = 5;
            requestFaceDownCards(activePlayer);
        }
        else if (mainCard instanceof TestCard){

            questDataPacket.questPhase = 6;
            if(questingPlayers.size() == 1){
                currentBid = Math.max(2, currentBid);

            }
            requestBid(activePlayer);
        }
        else{
            System.out.println("QuestStageMediator "+questStageID+": Error, invalid sponsor selection");
        }
    }

    private void requestBid(Player player) {
        questDataPacket.currentBid = currentBid;
        questDataPacket.activePlayerID = player.getPlayerID();

        Broadcaster.broadcast("(Backend )Request bid from player "+ player.getPlayerID());

    }

    public void submitBidDecision(boolean decision){
        int nextIndex;

        //update accordingly
        if(decision){
            currentBid++;
            nextIndex = questingPlayers.indexOf(activePlayer)+1;
        }
        else{
            nextIndex = questingPlayers.indexOf(activePlayer);
            questMediator.eliminate(activePlayer);
            questingPlayers.remove(activePlayer);
        }

        //proceed to next step
        if(questingPlayers.size() == 1){
            //winner found
            activePlayer = questingPlayers.get(0);
            requestBidPayment(activePlayer);
        }
        else if(questingPlayers.size() <= 0){
            //all eliminated
            questMediator.startNextStage();
        }
        else{
            nextIndex %= questingPlayers.size();
            activePlayer = questingPlayers.get(nextIndex);
            requestBid(activePlayer);
        }
    }

    private void requestBidPayment(Player activePlayer) {
        questDataPacket.activePlayerID = activePlayer.getPlayerID();
        questDataPacket.questPhase = 7;

        Broadcaster.broadcast("(Backend )Request test discard from "+ activePlayer.getPlayerID());

        //not sure if we should take into account free bids now or on frontend.
    }

    public void submitTestDiscard(){
        questMediator.startNextStage();

        Broadcaster.broadcast("submitTestDiscard ");

    }

    private void requestFaceDownCards(Player player) {
        questDataPacket.activePlayerID = player.getPlayerID();
        Broadcaster.broadcast("Request facedown cards");
    }

    //probably need error checking to be done before submission
    public void submitFaceDownCards(){
        System.out.println("QuestStageMediator: Player "+activePlayer.getPlayerID()+" playing cards on stage "+questStageID);
        //activePlayer.playCards();
        int nextPlayerIndex = questingPlayers.indexOf(activePlayer)+1;
        if(nextPlayerIndex < questingPlayers.size()){
            activePlayer = questingPlayers.get(nextPlayerIndex);
            requestFaceDownCards(activePlayer);
        }
        else{
            //move onto next phase
            executeStage();
        }
    }

    private void executeStage() {
        questDataPacket.revealedChallengeCards.get(questStageID).add(mainCard);
        for(Card wepCard : foeWeapons){
            questDataPacket.revealedChallengeCards.get(questStageID).add(wepCard);
        }

        foePower = calcFoePower();
        for(Player player : questingPlayers){
            player.revealCards();
            if(player.getCurrentBP() < foePower){
                questMediator.eliminate(player);
            }
            player.clearWeapons();
        }

        questMediator.startNextStage();
    }

    public int calcFoePower(){

        if(mainCard.getType().equals("Test")){
            return 0;
        }

        int result = questMediator.calcFoePower((FoeCard) mainCard);
        //add check here if the foe gets buffed by the quest
        for(WeaponCard card : foeWeapons){
            result += card.getDamage();
        }
        return result;
    }

    public void clear(){
        mainCard = null;
        foeWeapons.clear();
        currentBid = -1;
    }

    public int cardsSpent(){
        return foeWeapons.size() + 1;
    }

    public void init(ArrayList<Card> cards, QuestDataPacket data) {

        questDataPacket = data;
        if(cards.get(0) instanceof TestCard){
            mainCard = cards.get(0);
            TestCard testCard = (TestCard) mainCard;
            currentBid = testCard.getMinBid()-1;
        }
        else if(cards.get(0) instanceof FoeCard){

            mainCard = cards.get(0);
            cards.remove(0);
            for(Card card : cards){
                foeWeapons.add((WeaponCard) card);
            }

            foePower = calcFoePower();
        }
        else{
            System.out.println("QuestStageMediator "+questStageID+": Error, invalid sponsor selection");
        }
    }

    public Card getMainCard(){
        return mainCard;
    }
    public ArrayList<WeaponCard> getWeapons(){
        return foeWeapons;
    }

}
