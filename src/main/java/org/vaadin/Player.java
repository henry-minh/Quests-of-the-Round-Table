package org.vaadin;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.vaadin.card.*;

import java.util.ArrayList;

@Component
@Scope("prototype")
public class Player {
    private int playerID; //added getter, should not need setter, only set during construction
    private ArrayList<Card> hand = new ArrayList<>();
    private ArrayList<Card> cardsBeingPlayed = new ArrayList<>();
    private ArrayList<WeaponCard> activeWeapons = new ArrayList<>();
    private AmourCard activeAmour = null;
    private ArrayList<AllyCard> activeAllies = new ArrayList<>();

    @Autowired
    @Qualifier("Squire")
    private Rank squire;

    @Autowired
    @Qualifier("Knight")
    private Rank knight;

    @Autowired
    @Qualifier("Champion")
    private Rank champion;

    @Autowired
    @Qualifier("Squire")
    private Rank rank;

    private final String name; //private? getter?
    private boolean ready = false;
    private int shields = 0;

    @Autowired
    private Mediator mediator;

    public Player(String n, int pid) {

        name = n;
        playerID = pid;
    }

    public boolean isReady(){
        return ready;
    }

    public void readyUp(){
        ready = true;
    }

    public int handSize(){
        return hand.size();
    }

    public int getPlayerID(){
        return playerID;
    }

    public void drawAdventureCard(){
        hand.add(mediator.drawAdventureCard());
    }

    public void drawAdventureCard(Card c){
        hand.add(c);
    }

    public void drawAdvCards(int n){
        for(int i = 0 ; i < n ; i++){
            drawAdventureCard();
        }
    }

    public void discard(Card c){
        mediator.discard(c);
        hand.remove(c);
    }

    public int maxStages(){
        int count = 0;
        boolean hasTest = false;
        for(Card c : hand){
            if(c instanceof FoeCard){
                count++;
            }
            else if(c instanceof TestCard){
                hasTest = true;
            }
        }
        if(hasTest){
            count++;
        }
        return count;
    }

    public void playCards(){
        for(Card card : cardsBeingPlayed){
            if (card instanceof WeaponCard) {
                activeWeapons.add((WeaponCard) card);
            }
            else if (card instanceof AllyCard) {
                activeAllies.add((AllyCard) card);
            }
            else if (card instanceof AmourCard){
                activeAmour = (AmourCard) card;
            }
        }
        cardsBeingPlayed.clear();
    }

    public void revealCards(){
        //reveals weps amours allies
        //not sure how best to pass info to UI.
        //waiting for revealed/hidden flag to be added to cards
        //if we go that route
    }

    public String getName() {
        return name;
    }

    public int getCurrentBP(){
        int result = rank.getBP();
        for(WeaponCard wep : activeWeapons){
            System.out.println(wep);
            System.out.println("Damage: " + wep.getDamage());
            result += wep.getDamage();
        }
        if(activeAmour != null){
            //placeholder
            //result += activeAmour.getHP();
        }
        for(AllyCard ally : activeAllies){
            result += ally.getHP();
        }
        return result;
    }

    //placeholder, does GUI stuff. removing the player from the quest loop is done by
    //the Quest object.
    public void eliminate(){
        //inform player they have been eliminated maybe?
        //maybe remove
    }

    public void clearWeapons(){
        activeWeapons.clear();
    }

    public int getShields(){
        return shields;
    }

    public void setShields(int s){
        shields = s;
        if(shields < 0){
            shields = 0;
            return;
        }
        checkForRankUp();
        //check for leveling up here
    }

    private void checkForRankUp(){
        if(this.shields >= rank.getReqShields()){
            this.shields = this.shields - rank.getReqShields();
            rankUp();
        }
    }

    public void rankUp(){
        if(rank == squire){
            rank = knight;
        }
        else if(rank == knight){
            rank = champion;
        }
        else{
            winTheGame();
        }
    }

    private void winTheGame(){
        mediator.addWinner(this);
    }

    public int totalPlayerShields(){
        if(rank == squire){
            return shields;
        }
        if(rank == knight){
            return  shields + 5;
        }
        if(rank == champion){
            return shields + 12;
        }
        return -1;
    }

    public ArrayList<Card>getHand() {
        return hand;
    }


    public Rank getRank(){
        return this.rank;
    }
  
    public ArrayList<WeaponCard>getQuestStageCards() { return activeWeapons; }

    public void addQuestingCard(Card c) {
        hand.remove(c);
        cardsBeingPlayed.add(c);
    }

    public void addQuestingWeaponCard(WeaponCard c) {
        hand.remove(c);
        activeWeapons.add(c);
    }

    public Card takeBackCard() {
      Card c = activeWeapons.get(activeWeapons.size()-1);
        activeWeapons.remove(activeWeapons.size()-1);
      return c;
    }

    public boolean playedWeapon(Card c) {
        for(WeaponCard wep : activeWeapons){
            if (wep.getName() == c.getName()) {
                return true;
            }
        }
        return false;
    }


}
