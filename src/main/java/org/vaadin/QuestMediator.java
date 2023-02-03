package org.vaadin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.card.Card;
import org.vaadin.card.FoeCard;
import org.vaadin.card.QuestCard;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuestMediator {

    @Autowired
    private List<QuestStageMediator> stageList;
    @Autowired
    private Mediator mediator;

    private QuestDataPacket questDataPacket;

    private ArrayList<Player> questingPlayers = new ArrayList<>(); //removed final keyword as this will be changed

    private int stageCount = 0;
    private Player sponsor;
    private QuestCard questCard;

    public void init(QuestCard c, Player s, ArrayList<Player> players){
        questCard = c;
        stageCount = c.getStages();
        sponsor = s;
        questDataPacket = (QuestDataPacket) mediator.getStoryDataPacket();

        //rearrange player array to match turn order
        for (int i = players.indexOf(s)+1; i < players.size(); i++){
            questingPlayers.add(players.get(i));
        }
        for(int i = 0; i < players.indexOf(s); i++){
            questingPlayers.add(players.get(i));
        }

        //old version
        //questingPlayers.addAll(players);
        //questingPlayers.remove(sponsor);
        //possibly load in name of quest and what foes get boosted?
    }

    public void start(){
        //sponsor chooses encounters
        populateQuestStages();
        //inviteToQuest(questingPlayers.get(0));
    }

    private void populateQuestStages() {
        questDataPacket.questPhase = 2;
        questDataPacket.activePlayerID = sponsor.getPlayerID();
        Broadcaster.broadcast("Waiting for sponsor to choose stages");

    }

    private void playCardsForStage() {
        questDataPacket.questPhase = 5;
        questDataPacket.activePlayerID = questingPlayers.get(0).getPlayerID() ;
        Broadcaster.broadcast("Waiting for sponsor to choose stages");

    }

    //going to assume the correctness of the submission is made in MainView,
    //as well calling the appropriate discard methods
    // (technically the cards will still read as in the hand of the sponsor but I think thats ok?)
    //double ArrayList is using stage first, followed by the cards, the first card should be the foe,
    // followed by weapons
    //to populate this it would be doubleArrayListName.get(stagenumber).add(card)
    public void submitQuestStages(ArrayList<ArrayList<Card>> submission){
        for(int i = 0; i < stageCount; i++){
            QuestStageMediator currStage = stageList.get(i);
            currStage.init(submission.get(i), questDataPacket);
        }
        //add a function that updates GUI with number of facedown cards.

        //players decide to go on quest, pass sponsor to say who can't go
        inviteToQuest(questingPlayers.get(0));
    }

    private void inviteToQuest(Player p){
        questDataPacket.questPhase = 4;
        questDataPacket.activePlayerID = p.getPlayerID();
        Broadcaster.broadcast("Invite Stage started");
    }
    public void submitInviteDecision(boolean confirmation){
        //determine submitter
        Player submitter = null;
        for(Player player : questingPlayers){
            if(player.getPlayerID() == questDataPacket.activePlayerID){
                submitter = player;
            }
        }

        int index = questingPlayers.indexOf(submitter);
        //if denied
        if(!confirmation){
            questingPlayers.remove(submitter);
            if (index == questingPlayers.size()){
                startNextStage();
            }
            else {
                inviteToQuest(questingPlayers.get(index));
            }
        }
        //if confirmed
        else{
            if(index == questingPlayers.size()-1){
                startNextStage();
            }
            else{
                inviteToQuest(questingPlayers.get(index+1));
            }
        }
    }

    public void startStage(int i) {
        if(i < stageCount) {
            questDataPacket.currentStage = i;
            stageList.get(i).start(questingPlayers);
        }
    }

    public void eliminate(Player p) {
        //notify player they have been eliminated from the quest
        p.eliminate();
        questingPlayers.remove(p);
    }

    //test method
    public void printStages(){
        for(QuestStageMediator stage : stageList){
            System.out.println("Found Stage "+stageList.indexOf(stage));
        }
    }

    private int calcCardsSpent(){
        int result = 0;
        for(QuestStageMediator stage : stageList){
            result += stage.cardsSpent();
        }
        return result;
    }

    public void clear(){
        sponsor = null;
        questingPlayers.clear();
        questCard = null;
        for(QuestStageMediator stage : stageList){
            stage.clear();
        }
        questDataPacket = null;
    }

    public void startNextStage() {
        int nextStage = questDataPacket.currentStage+1;
        if(nextStage < stageCount && questingPlayers.size() > 0){
            startStage(nextStage);
        }
        else{
            wrapUp();
        }
    }

    public boolean isQuesting(Player p) {
        for (Player q : questingPlayers) {
            if (q.getPlayerID() == p.getPlayerID()) {
                return true;
            }
        }
        return false;
    }

    public void submitQuestStageCards() {
        stageList.get(questDataPacket.currentStage).submitFaceDownCards();
    }


    private void wrapUp() {
        int kingRecExtra = 0;
        if(mediator.getKingRec() == true){
            kingRecExtra = 2;
            mediator.setKingRec(false);
        }

        for(Player player : questingPlayers){
            player.setShields(stageCount + kingRecExtra);
        }
        sponsor.drawAdvCards(calcCardsSpent()+stageCount);
        System.out.println("QuestMediator.wrapUp: Quest ending");
        //current player should be told to press end turn
        Broadcaster.broadcast("Quest Over");
        mediator.endTurn();
    }
    public int getSponsorId(){
        return questDataPacket.getSponsorId();
    }

    public int getStageCount() {
        return questDataPacket.getStageCount();
    }

    public int calcFoePower(FoeCard foe){
        if (questCard.getFoes().contains(foe.getName())){
            return foe.getHighHP();
        }
        return foe.getLowHP();
    }
}
