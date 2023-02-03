package org.vaadin;


import com.vaadin.flow.component.EventData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.card.*;
import org.vaadin.event.*;

import java.util.ArrayList;
import java.util.Collections;


@Component
public class Mediator {


    @Autowired
    private QuestMediator questMediator;
    @Autowired
    private TournamentMediator tournamentMediator;

    private Boolean kingRec = false;

    private final ArrayList<Player> players = new ArrayList<>();
    private final ArrayList<Integer> winnerIDs = new ArrayList<>();
    private ArrayList<Card> adventureDeck = createAdvDeck();
    private final ArrayList<Card> advDiscardPile = new ArrayList<>();

    //private final ArrayList<Card> storyDeck = createStoryDeck();
    private ArrayList<Card> storyDeck = createTestStoryDeck();
    private ArrayList<Card> storyDiscardPile = new ArrayList<>();
    private Card currentStoryCard = null;

    private Player currentPlayer = null;
    private int currentTurn = 0;
    private boolean turnInProgress = false;
    private boolean readyToEndTurn = false;
    private int kingsCallCount = 0;

    private StoryDataPacket storyDataPacket;

    public void addPlayers(ArrayList<Player> p){
        players.addAll(p);
    }
    public Player getPlayer(int i){
        return players.get(i);
    }
    public int getPlayerCount(){
        return players.size();
    }
    public void setReadyToEndTurn(boolean flag){
        readyToEndTurn = flag;
    }

    //Call this method when you want to render the page
    //current implementation requires playerid and index in array to be the same
    public GameDataPacket getGameState(int pid){

        ArrayList<Integer> handSizes = new ArrayList<>();
        for(Player player : players){
            handSizes.add(player.handSize());
        }

        ArrayList<String> playerRanks = new ArrayList<>();
        for(Player player : players){
            playerRanks.add(player.getRank().getName());
        }

        ArrayList<Integer> playerShields = new ArrayList<>();
        for(Player player : players){
            playerShields.add(player.getShields());
        }

        return new GameDataPacket(currentPlayer.getPlayerID(),
                adventureDeck.size(),
                advDiscardPile,
                players.get(pid),
                handSizes,
                playerShields,
                playerRanks,
                winnerIDs,
                readyToEndTurn,
                currentStoryCard,
                players.size(),
                storyDataPacket
                );
    }


    public int startTurn(Player p){
        if(turnInProgress){
            System.out.println("Cannot start "+p+"'s turn, other turn in progress");
            return -1;
        }

        turnInProgress = true;
        currentPlayer = p;

        System.out.println("Start of "+p.getName()+"'s turn");

        currentStoryCard = drawStoryCard();
        //add a line here to push the story to the GUI
        //this is where other routes will be added as we move on
        if(currentStoryCard.getType() == "Quest"){
            handleQuestDraw();
        }else if(currentStoryCard.getType() == "Event"){
            handleEventDraw();
        }else if(currentStoryCard.getType() == "Tournament"){
            handleTournamentDraw();
        }


        return 0;
    }

    public void endTurn(){
        turnInProgress = false;
        storyDataPacket = null;
        readyToEndTurn = false;
        questMediator.clear();
        discardStoryCard();
        System.out.println(currentPlayer.getName()+"'s turn has ended");
        currentTurn++;
        startTurn(players.get(currentTurn % players.size()));
    }

    public Card drawAdventureCard(){

        Card c = adventureDeck.get(0);
        adventureDeck.remove(0);

        if (adventureDeck.isEmpty()){
            System.out.println("The adventure deck is Empty!\nRefilling and Shuffling Adventure Deck");
            adventureDeck = (ArrayList<Card>) advDiscardPile.clone();
            Collections.shuffle(adventureDeck);
            advDiscardPile.clear();
        }

        return c;
    }
    public Card drawStoryCard(){
        if (storyDeck.isEmpty()){
            System.out.println("The story deck is Empty!\nRefilling and Shuffling story Deck");
            storyDeck = (ArrayList<Card>) storyDiscardPile.clone();
            Collections.shuffle(storyDeck);
            storyDiscardPile.clear();
        }

        System.out.println(storyDeck.get(0));
        Card drawnCard = storyDeck.get(0);
        storyDeck.remove(0);

        return drawnCard;
    }
    public void discard(Card c){
        advDiscardPile.add(c);
    }
    private void discardStoryCard(){
        storyDiscardPile.add(currentStoryCard);
        currentStoryCard = null;
    }


    private void handleQuestDraw(){
        //create QuestDataPacket
        QuestDataPacket questDataPacket = new QuestDataPacket((QuestCard) currentStoryCard);
        storyDataPacket = questDataPacket;
        questDataPacket.questPhase = 1;
        //first go around table until someone sponsors
        questDataPacket.activePlayerID = currentPlayer.getPlayerID();
        Broadcaster.broadcast("confirm sponsorship");
        //quest object always exists in the context. init sets its state to be for this card
    }

    private void handleEventDraw(){
        EventStory event;

        if(currentStoryCard.getName() == "King's Call to Arms"){
            EventDataPacket eventDataPacket = new EventDataPacket(true);
            storyDataPacket = eventDataPacket;
            event = new EventKingCallToArms(this);
            event.event();
            Broadcaster.broadcast("Choose cards for king call to arms event");
        }else if(currentStoryCard.getName() == "Court Called to Camelot"){
            event = new EventCourtCamelot(this);
            event.event();
        }else if(currentStoryCard.getName() == "King's Recognition"){
            event = new EventKingRec(this);
            event.event();
        }else if(currentStoryCard.getName() == "Plague"){
            event = new EventPlague(this);
            event.event();
        }else if(currentStoryCard.getName() == "Prosperity Throughout the Realm"){
            event = new EventProsperity(this);
            event.event();
        }else if(currentStoryCard.getName() == "Pox"){
            event = new EventPox(this);
            event.event();
        }else if(currentStoryCard.getName() == "Queen's Favor"){
            event = new EventQueenFavor(this);
            event.event();
        }else if(currentStoryCard.getName() == "Chivalrous Deed"){
            event = new EventChivalrous(this);
            event.event();
        }else{
            System.out.println("EVENT CARD NOT FOUND");
        }
        //tell mediator that turn can be ended
        if(!(storyDataPacket instanceof EventDataPacket)){
            readyToEndTurn = true;
            Broadcaster.broadcast("event turn end");
        }
    }
    public void kingsCallDiscarded(){
        EventDataPacket eventDataPacket;
        if(storyDataPacket instanceof EventDataPacket){
            eventDataPacket = (EventDataPacket) storyDataPacket;
        }
        else{
            System.out.println("Mediator: Attempt to submit king's call outside of Event turn");
            return;
        }
        kingsCallCount++;
        if(kingsCallCount >= eventDataPacket.kingCallHighestRanked.size()){
            readyToEndTurn = true;
            kingsCallCount = 0;
            Broadcaster.broadcast("event turn end");
        }
    }

    public void handleTournamentDraw(){

        /*STEPS:
        1.ask player to join
            1.1 Drawer gets asked first
            1.2 If only 1 player enters, is awarded 1 shield + extra
        2.All tournament players draw adventure card
        3.Pick cards they want to play
            3.1 No doubles and only 1 amour
            3.2 Can play zero cards (Only count rank)
        4.Highest ranked player (Not counting allies already in play) wins -> potential tie
        5.Winner receives #shields = #numPlayers who entered tourney + extra

        IF TIE OCCURS:
        1. Discard weapon cards in play
        2. Pick cards they want to play
        3. Repeat step 4
            3.1 If tie again, all players in second round receive #shields = #numPlayers
        */

        storyDataPacket = new TournamentDataPacket();
        tournamentMediator.init((TournamentCard) currentStoryCard, players, currentPlayer, (TournamentDataPacket) storyDataPacket);
    }

    public void confirmSponsor(){
        QuestDataPacket questDataPacket;
        if(getStoryDataPacket() instanceof QuestDataPacket){
            questDataPacket = (QuestDataPacket) getStoryDataPacket();
        }
        else{
            System.out.println("Mediator: Attempt to sponsor quest when no quest available");
            return;
        }
        questDataPacket.sponsorID = questDataPacket.activePlayerID;
        questMediator.init((QuestCard) currentStoryCard, players.get(questDataPacket.sponsorID), players);
        questMediator.start();
    }

    public void refuseSponsor(){
        QuestDataPacket questDataPacket;
        if(getStoryDataPacket() instanceof QuestDataPacket){
            questDataPacket = (QuestDataPacket) getStoryDataPacket();
        }
        else{
            System.out.println("Mediator: Attempt to refuse quest sponsorship when no quest available");
            return;
        }
        questDataPacket.activePlayerID++;
        questDataPacket.activePlayerID = questDataPacket.activePlayerID % players.size();
        if(questDataPacket.activePlayerID == currentPlayer.getPlayerID()){
            questDataPacket.activePlayerID = -1;
            System.out.println("Mediator: no one wants to sponsor, passing turn");
            endTurn();
            return;
        }
        Broadcaster.broadcast("next sponsor");
    }

    //used in construction
    //when adding new card update here
    private ArrayList<Card> createAdvDeck(){
        //should be changed to AdvCardFactory when implemented
        ArrayList<Card> deck = new ArrayList<>();

        WeaponCardFactory weaponFactory = new WeaponCardFactory();
        FoeCardFactory foeFactory = new FoeCardFactory();
        TestCardFactory testFactory = new TestCardFactory();
        AllyCardFactory allyFactory = new AllyCardFactory();

        for (int i = 0; i < 2; i++) {
            deck.add(weaponFactory.createCard("Excalibur"));

            deck.add(foeFactory.createCard("Giant"));
            deck.add(foeFactory.createCard("Green Knight"));
        }

        for (int i = 0; i < 6; i++) {
            deck.add(weaponFactory.createCard("Lance"));
            deck.add(weaponFactory.createCard("Dagger"));

            deck.add(foeFactory.createCard("Evil Knight"));
        }

        for (int i = 0; i < 8; i++) {
            deck.add(weaponFactory.createCard("Battle-ax"));

            deck.add(foeFactory.createCard("Saxon Knight"));
            deck.add(foeFactory.createCard("Thieves"));
        }

        for (int i = 0; i < 16; i++) {
            deck.add(weaponFactory.createCard("Sword"));
        }

        for (int i = 0; i < 11; i++) {
            deck.add(weaponFactory.createCard("Horse"));
        }

        for (int i = 0; i < 1; i++) {
            deck.add(foeFactory.createCard("Dragon"));
        }

        for (int i = 0; i < 3; i++) {
            deck.add(foeFactory.createCard("Black Knight"));
        }

        for (int i = 0; i < 7; i++) {
            deck.add(foeFactory.createCard("Robber Knight"));
        }

        for (int i = 0; i < 5; i++) {
            deck.add(foeFactory.createCard("Saxons"));
        }

        for (int i = 0; i < 4; i++) {
            deck.add(foeFactory.createCard("Boar"));

            //Mordred not yet implemented
            //deck.add(foeFactory.createCard("Mordred"));
        }

        //Tests
        for (int i = 0; i < 2; i++) {
            deck.add(testFactory.createCard("Test of Valor"));
            deck.add(testFactory.createCard("Test of Temptation"));
            deck.add(testFactory.createCard("Test of Morgan Le Fey"));
            deck.add(testFactory.createCard("Test of the Questing Beast"));
        }

        /*
        //Allies
        deck.add(allyFactory.createCard("Sir Galahad"));
        deck.add(allyFactory.createCard("Sir Lancelot"));
        deck.add(allyFactory.createCard("King Arthur"));
        deck.add(allyFactory.createCard("Sir Tristan"));
        deck.add(allyFactory.createCard("King Pellinore"));
        deck.add(allyFactory.createCard("Sir Gawain"));
        deck.add(allyFactory.createCard("Sir Percival"));
        deck.add(allyFactory.createCard("Queen Guinevere"));
        deck.add(allyFactory.createCard("Queen Iseult"));
        deck.add(allyFactory.createCard("Merlin"));
         */

        Collections.shuffle(deck);
        return deck;
    }
    public ArrayList<Card> createStoryDeck() {
        QuestCardFactory questFactory = new QuestCardFactory();
        EventCardFactory eventFactory = new EventCardFactory();
        TournamentCardFactory tournamentFactory = new TournamentCardFactory();
        ArrayList<Card> deck = new ArrayList<>();

        //Quest Cards
        deck.add(questFactory.createCard("Search for the Holy Grail"));
        deck.add(questFactory.createCard("Test of the Green Knight"));
        deck.add(questFactory.createCard("Search for the Questing Beast"));
        deck.add(questFactory.createCard("Defend the Queen's Honor"));
        deck.add(questFactory.createCard("Rescue the Fair Maiden"));
        deck.add(questFactory.createCard("Journey through the Enchanted Forest"));
        deck.add(questFactory.createCard("Slay the Dragon"));

        for (int i = 0; i < 2; i++) {
            deck.add(questFactory.createCard("Vanquish King Arthur's Enemies"));
            deck.add(questFactory.createCard("Boar Hunt"));
            deck.add(questFactory.createCard("Repel the Saxon Raiders"));

            //deck.add(eventFactory.createCard("Court Called to Camelot"));
            deck.add(eventFactory.createCard("King's Recognition"));
            deck.add(eventFactory.createCard("Queen's Favor"));
        }

        deck.add(eventFactory.createCard("King's Call to Arms"));
        deck.add(eventFactory.createCard("Plague"));
        deck.add(eventFactory.createCard("Prosperity Throughout the Realm"));
        deck.add(eventFactory.createCard("Pox"));
        deck.add(eventFactory.createCard("Chivalrous Deed"));


        deck.add(tournamentFactory.createCard("Tournament At Camelot"));
        deck.add(tournamentFactory.createCard("Tournament At York"));
        deck.add(tournamentFactory.createCard("Tournament At Orkney"));
        deck.add(tournamentFactory.createCard("Tournament At Tintagel"));


        Collections.shuffle(deck);

        return deck;
    }

    public ArrayList<Card> createTestStoryDeck() {
        QuestCardFactory questFactory = new QuestCardFactory();
        EventCardFactory eventFactory = new EventCardFactory();
        TournamentCardFactory tournamentFactory = new TournamentCardFactory();
        ArrayList<Card> deck = new ArrayList<>();

        //FOR TESTING
        //deck.add(eventFactory.createCard("King's Call to Arms"));
        deck.add(eventFactory.createCard("Prosperity Throughout the Realm"));
        deck.add(tournamentFactory.createCard("Tournament At Camelot"));
        deck.add(questFactory.createCard("Search for the Holy Grail"));

        return deck;
    }

    public StoryDataPacket getStoryDataPacket() {
        return storyDataPacket;
    }

    public Player getCurrPlayer(){
        return currentPlayer;
    }

    public void addWinner(Player p){
        winnerIDs.add(p.getPlayerID());
        Broadcaster.broadcast("Someone has won the game");
    }

    public ArrayList<Integer> getWinnerIds(){
        return winnerIDs;
    }

    public void setKingRec(Boolean b){
        kingRec = b;
    }
    public Boolean getKingRec(){
        return kingRec;
    }
}
