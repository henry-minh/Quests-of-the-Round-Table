package org.vaadin;


import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.page.Push;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.shared.Registration;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.atmosphere.interceptor.AtmosphereResourceStateRecovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.card.*;

import java.util.ArrayList;
import java.util.List;


@Push
@Route(value = "gameStart/:id([0-3]*)")
@PageTitle("Quests Of The Round Table")
@CssImport("./styles/styles.css")

public class MainView extends VerticalLayout {
    @Autowired
    Mediator mediator;
    @Autowired
    QuestMediator questMediator;
    @Autowired
    List<QuestStageMediator> stageList;
    @Autowired
    TournamentMediator tournamentMediator;
    GameDataPacket gameState;
    H2 instructionText;
    HorizontalLayout instructionContainer = new HorizontalLayout();
    HorizontalLayout boardContainer = new HorizontalLayout();                            //(contains the board)
    HorizontalLayout deckRowContainer = new HorizontalLayout();                          //contains Adventure/Story Deck and Discard Piles)
    HorizontalLayout DrawDiscardContainer = new HorizontalLayout();
    HorizontalLayout horizontalHand1 = new HorizontalLayout();      // main player hand
    Paragraph playerDescription = new Paragraph();                 //Display Player name and card count
    VerticalLayout subPlayersContainer = new VerticalLayout();
    HorizontalLayout turnInfoContainer = new HorizontalLayout();
    VerticalLayout cardInfoContainer = new VerticalLayout();
    VerticalLayout getPlayerCards = new VerticalLayout();
    HorizontalLayout playedCardsContainer = new HorizontalLayout();
    int playerCount = 0;
    Registration broadcasterRegistration;   //used to update UI
    ArrayList<ArrayList<Card>> sponsorStageSetup = new ArrayList<ArrayList<Card>>(); //used for sponsor
    boolean flag = false;   //used for kinfs call event
    //int testDiscardCount = 0;

    //////////////////////////////////////////////////////////
    //                     Initial Load                     //
    //////////////////////////////////////////////////////////
    public MainView() {
        for (int i = 0; i < 5; i++) {
            ArrayList<Card> stage = new ArrayList<>();
            sponsorStageSetup.add(stage);
        }

        Div load = new Div();
        load.setClassName("load");

        Div loadBtn = new Div();
        loadBtn.addClassName("btn");
        loadBtn.add(new Span("Load Board"));

        Div dot = new Div();
        dot.addClassName("dot");

        load.add(loadBtn);
        loadBtn.add(dot);

        load.addClickListener(click -> {
            loadGame();
            load.setEnabled(false);
            load.setVisible(false);
        });
        add(load);
        this.setHorizontalComponentAlignment(Alignment.CENTER,load);
    }

    private void loadGame() {
        loadStart();

        ////////////////
        //Instructions//
        ////////////////
        instructionText = new H2("Quests Of The Round Table");                       //this literally has no purpose right now, it's just to demonstrate the add functionality of SharedBoard
        instructionText.getElement().setAttribute("class", "selected");
        instructionContainer.add(instructionText);
        add(instructionContainer);
        //////////////
        //  Board   //
        //////////////

        boardContainer.getElement().setAttribute("class", "boardDimension");
        //boardContainer.setJustifyContentMode(JustifyContentMode.EVENLY);
        //boardContainer.setJustifyContentMode();
        add(boardContainer);
        ////////////////
        //Deck-Discard//
        ////////////////
        // ROW 2
        add(deckRowContainer);
        //adventure deck
        //create vertical layout which will stack card back ontop of a <p> which shows the user what type of deck/discard pile it is (adventure or story)
        VerticalLayout adventureDeckVertical = new VerticalLayout();
        Image adventureDeck = new Image("images/default.png", "default placeholder");
        adventureDeck.setWidth("75px");
        adventureDeck.getElement().setAttribute("class", "adventureDeck");
        adventureDeckVertical.add(adventureDeck, new Paragraph("Adventure Deck"));

        //adventure discard
        // NOTE, Of the 4 piles in the Deck/Discard Row, Story Discard Is the only one with a click event listener to display it's info (others will be implemented according with further sprint specifications)
        VerticalLayout adventureDiscardVertical = new VerticalLayout();
        Image adventureDiscard = new Image("images/default.png", "default placeholder");
        adventureDiscard.setWidth("75px");

        adventureDiscard.getElement().setAttribute("class", "adventureDiscard");
        adventureDiscard.getElement().addEventListener("click", e -> {
            displayAdventureDiscardPile();
        });
        adventureDiscardVertical.add(adventureDiscard, new Paragraph("Adventure Discard"));

        //story deck
        VerticalLayout storyDeckVertical = new VerticalLayout();
        Image storyDeck = new Image("images/default.png", "default placeholder");
        storyDeck.setWidth("75px");
        storyDeck.getElement().setAttribute("class", "storyDeck");
        storyDeckVertical.add(storyDeck, new Paragraph("Story Deck"));

        //story discard
        VerticalLayout storyDiscardVertical = new VerticalLayout();
        Image storyDiscard = new Image("images/default.png", "default placeholder");
        storyDiscard.setWidth("75px");
        storyDiscard.getElement().setAttribute("class", "storyDiscard");
        storyDiscardVertical.add(storyDiscard, new Paragraph("Story  Discard"));

        deckRowContainer.add(adventureDeckVertical, adventureDiscardVertical, storyDeckVertical, storyDiscardVertical); // adding the 4 vertical elements into ROW 2.
        add(turnInfoContainer);
        turnInfoContainer.add(new Paragraph("test"));

        //adding players into the game
        loadPlayerCards();
        loadStory();
        loadAskToSponsor();
        loadKingCall();
        loadAskToTourney();
        rankUpButton();
        if(gameState.you.getHand().size() > 12){
            loadDiscardOverdraw();
        }


        add(subPlayersContainer);
        add(cardInfoContainer);
    }

    private void loadStart() {
        String playerIdStr = getIdFromUrl();
        gameState = mediator.getGameState(Integer.parseInt(playerIdStr));           //this will be handled from the setup page tristan is working on
        loadMainPlayerDeck();
        loadSubPlayer();

        turnInfoContainer.removeAll();
        if (gameState.you.getPlayerID() == gameState.currentPlayerID && gameState.readyToEndTurn == true) {

            Button endTurnButton = new Button("end turn");
            turnInfoContainer.add(endTurnButton);
            endTurnButton.addClickListener(clickEvent -> {
                mediator.endTurn();
                Broadcaster.broadcast("Handling end Turn Button");
            });

        }
        turnInfoContainer.add(new Paragraph("Player Turn: " + gameState.currentPlayerID));
    }

    //////////////////////////////////////////////////////////
    //                      Functions                       //
    //////////////////////////////////////////////////////////

    private String getPlayerInfo(int PlayerNum, String rank, int shields, int playerHandSize) {
        return String.format("Player: %d Rank: %s Shields: %d Hand Size: %d", PlayerNum, rank, shields, playerHandSize);
    }

    private void displaySelectedCard(Card card, int index) {
        cardInfoContainer.removeAll();
        Paragraph cardDescription = new Paragraph(card.toString());
        String classname = "selected" + Integer.toString(index);
        cardDescription.getElement().setAttribute("class", classname);
        cardInfoContainer.add(cardDescription);
    }

    private void displayAdventureDiscardPile() {
        cardInfoContainer.removeAll();
        H2 storyDiscard = new H2("Adventure Discard Size: " + gameState.advDiscard.size());
        cardInfoContainer.add(storyDiscard);
        for (int i = 0; i < gameState.advDiscard.size(); i++) {
            Paragraph discardedCard = new Paragraph(gameState.advDiscard.get(i).toString());
            cardInfoContainer.add(discardedCard);
        }
    }

    //used to get stage power requirement
    //can be reused to see players cards power level
    private int getDeckPower(ArrayList<Card> deck) {
        int power = 0;
        for (int i = 0; i < deck.size(); i++) {
            if (deck.get(i).getType().equals("Weapon")) {
                power = power + ((WeaponCard) deck.get(i)).getDamage();
            }
            if (deck.get(i).getType().equals("Foe")) {
                power += questMediator.calcFoePower((FoeCard) deck.get(i));
            }
        }
        return power;
    }

    private boolean checkStageOrderValid() {
        boolean isStageOrderValid = true;
        for (int j = 0; j < questMediator.getStageCount(); j++) {
            if (sponsorStageSetup.get(j).isEmpty()) {
                isStageOrderValid = false;
            } else if (!sponsorStageSetup.get(j).get(0).getType().equals("Foe")) {
                if (!sponsorStageSetup.get(j).get(0).getType().equals("Test")) {
                    isStageOrderValid = false;
                }

            }
            if (j + 1 != questMediator.getStageCount()) {
                if (getDeckPower(sponsorStageSetup.get(j)) > getDeckPower(sponsorStageSetup.get(j + 1))) {
                    if (!sponsorStageSetup.get(j + 1).get(0).getType().equals("Test")) {
                        isStageOrderValid = false;
                    }

                }
            }
        }
        return isStageOrderValid;
    }
    //////////////////////////////////////////////////////////
    //                     Push Functions                   //
    //////////////////////////////////////////////////////////

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = Broadcaster.register(newMessage -> {
            ui.access(() -> pushUI());
        });
    }

    private void pushUI() {
        refreshGameStateData();
        loadMainPlayerDeck();
        loadSubPlayer();
        loadEndTurn();
        loadStory();

        if(gameState.you.getHand().size() > 12 && gameState.readyToEndTurn){
            loadDiscardOverdraw();
        }

        if(gameState.readyToEndTurn == false){
            if(gameState.you.getHand().size() > 12){
                loadDiscardOverdraw();
        }else {
            loadAskToSponsor();
            loadQuestStages();
            loadAskToQuest();
            getPlayerQuestCards();
            loadKingCall();

            loadDiscardButton();
            askToBid();
            testBidPayment();

            loadAskToTourney();
            getPlayerTourneyCards();
            }
        }

        //General Game Data

        //Quests

        //Events

        //Tournaments


        //run stage
        //rankUpButton();
        loadWin();
    }

    private void refreshGameStateData() {
        gameState = mediator.getGameState(Integer.parseInt(getIdFromUrl()));
    }


    //////////////////////////////////////////////////////////
    //       Update functions used by Push Functions        //
    //////////////////////////////////////////////////////////
    private void loadDiscardButton() {
        DrawDiscardContainer.removeAll();
        DrawDiscardContainer.add(playerDescription);
        if (gameState.you.getHand().size() > 12) {
            Button discardButton = new Button("Discard");
            discardButton.getElement().addEventListener("click", e -> {
                String str = playerDescription.getText();
                if (str.contains(">")) {
                    int index = Integer.parseInt(str.substring(str.lastIndexOf(">") + 2));
                    gameState.you.discard(gameState.you.getHand().get(index));
                }
                loadMainPlayerDeck();
                Broadcaster.broadcast("default value");

                //Removes Discard button when the user has the proper amount of cards
                if (gameState.you.handSize() <= 12) {
                    DrawDiscardContainer.remove(discardButton);
                    instructionText.setText("Quests Of The Round Table");
                }
                Broadcaster.broadcast("Handling Discard Button popping up");
            });
            DrawDiscardContainer.add(discardButton);
        }

    }

    private void loadDiscardOverdraw(){

        boardContainer.removeClassName("PromptDiv");

        Div discardPrompt = new Div();
        discardPrompt.setClassName("PromptDiv");

        Span discardSpan = new Span();
        discardSpan.addClassName("promptSpan");
        discardPrompt.add(discardSpan);

        Div discardBtn = new Div();
        discardBtn.addClassName("btn");
        Span yes = new Span("Discard");
        discardBtn.add(yes);

        Div discardDot = new Div();
        discardDot.addClassName("dot");

        discardBtn.add(discardDot);

        discardPrompt.add(discardBtn);

        discardSpan.setText("Discard Cards Until You have 12");
        discardBtn.addClickListener(clickEvent -> {
            String str = playerDescription.getText();
            if (str.contains(">")) {
                int index = Integer.parseInt(str.substring(str.lastIndexOf(">") + 2));

                gameState.you.discard(gameState.you.getHand().get(index));
                boardContainer.remove(discardPrompt);

                mediator.kingsCallDiscarded();
                loadMainPlayerDeck();
                pushUI();
            }
        });
        boardContainer.add(discardPrompt);
        loadMainPlayerDeck();

    }

    private void rankUpButton(){


        Button rankUp = new Button("rank up");

        rankUp.addClickListener(click -> gameState.you.rankUp());

        instructionContainer.remove(rankUp);
        instructionContainer.add(rankUp);
    }

    private void loadWin() {
        if(mediator.getWinnerIds().size() < 1){
            return;
        }
        this.removeAll();
        HorizontalLayout winners = new HorizontalLayout();

        Div winnerPrompt = new Div();
        winnerPrompt.setClassName("PromptDiv");

        Span winnerSpan = new Span("Winners: \n");
        winnerSpan.addClassName("promptSpan");
        winnerPrompt.add(winnerSpan);

        String winnerString = "";

        ArrayList<Integer> winnersArr = mediator.getWinnerIds();

        for (int i = 0;i < winnersArr.size(); i++){
            Player winnerPlayer = mediator.getPlayer(winnersArr.get(i));
            Span winner = new Span(winnerPlayer.getName());
            winner.addClassName("promptWinnersSpan");
            winnerPrompt.add(winner);

        }

        this.add(winnerPrompt);
        this.setHorizontalComponentAlignment(Alignment.CENTER,winnerPrompt);

    }

    private void loadAskToQuest() {

        QuestDataPacket questDataPacket;
        if (gameState.storyDataPacket instanceof QuestDataPacket) {
            questDataPacket = (QuestDataPacket) gameState.storyDataPacket;
        } else {
            //.out.println("MainView: Attempt to load quest when no quest available");
            return;
        }

        if (questDataPacket.questPhase == 4 && questDataPacket.activePlayerID == gameState.you.getPlayerID()) {
            System.out.println("ASK to quest" + getIdFromUrl());
            /*
            Create prompt on that page
             */
            Div questPrompt = new Div();
            questPrompt.setClassName("PromptDiv");

            Span questSpan = new Span("Would you like to join the quest?");
            questSpan.addClassName("promptSpan");
            questPrompt.add(questSpan);

            Div yesBtn = new Div();
            yesBtn.addClassName("yesBtn");
            Span yes = new Span("Yes");
            yesBtn.add(yes);

            Div yesDot = new Div();
            yesDot.addClassName("yesDot");

            yesBtn.add(yesDot);

            Div noBtn = new Div();
            noBtn.addClassName("noBtn");
            Span no = new Span("No");
            noBtn.add(no);

            Div noDot = new Div();
            noDot.addClassName("noDot");

            noBtn.add(noDot);

            questPrompt.add(noBtn, yesBtn);

            noBtn.addClickListener(clickEvent -> {
                boardContainer.remove(questPrompt);
                choosesToQuest(false);
            });
            yesBtn.addClickListener(click -> {
                boardContainer.remove(questPrompt);
                choosesToQuest(true);
            });
            questPrompt.setMinHeight(10, Unit.EM);
            questPrompt.setMinWidth(20, Unit.EM);
            boardContainer.add(questPrompt);
            //boardContainer.

        }

    }

    private void loadAskToTourney(){
        TournamentDataPacket tournamentDataPacket;
        if (gameState.storyDataPacket instanceof TournamentDataPacket) {
            tournamentDataPacket = (TournamentDataPacket) gameState.storyDataPacket;
        } else {
            //System.out.println("MainView: Attempt to load tourney when no quest available");
            return;
        }

        if(tournamentDataPacket.tourneyPhase == 1 && tournamentDataPacket.activePlayerID == gameState.you.getPlayerID()){
            Div tournamentPrompt = new Div();
            tournamentPrompt.setClassName("PromptDiv");

            Span tournamentSpan = new Span("Would you like to join the tournament?");
            tournamentSpan.addClassName("promptSpan");
            tournamentPrompt.add(tournamentSpan);

            Div yesBtn = new Div();
            yesBtn.addClassName("yesBtn");
            Span yes = new Span("Yes");
            yesBtn.add(yes);

            Div yesDot = new Div();
            yesDot.addClassName("yesDot");

            yesBtn.add(yesDot);

            Div noBtn = new Div();
            noBtn.addClassName("noBtn");
            Span no = new Span("No");
            noBtn.add(no);

            Div noDot = new Div();
            noDot.addClassName("noDot");

            noBtn.add(noDot);

            tournamentPrompt.add(noBtn, yesBtn);

            noBtn.addClickListener(clickEvent -> {
                boardContainer.remove(tournamentPrompt);
                choosesToTourney(false);
            });
            yesBtn.addClickListener(click -> {
                boardContainer.remove(tournamentPrompt);
                choosesToTourney(true);
            });
            tournamentPrompt.setMinHeight(10, Unit.EM);
            tournamentPrompt.setMinWidth(20, Unit.EM);
            boardContainer.add(tournamentPrompt);
        }
    }

    private void loadAskToSponsor() {
        QuestDataPacket questDataPacket;
        if (gameState.storyDataPacket instanceof QuestDataPacket) {
            questDataPacket = (QuestDataPacket) gameState.storyDataPacket;
        } else {
            //System.out.println("MainView: Attempt to load quest when no quest available");
            return;
        }
        if (questDataPacket.questPhase == 1 && questDataPacket.activePlayerID == gameState.you.getPlayerID()) {
            /*
            Create prompt on that page
             */
            Div sponsorPrompt = new Div();
            sponsorPrompt.setClassName("PromptDiv");

            Span sponsorSpan = new Span("Would you like to sponsor the quest?");
            sponsorSpan.addClassName("promptSpan");
            sponsorPrompt.add(sponsorSpan);

            Div yesBtn = new Div();
            yesBtn.addClassName("yesBtn");
            Span yes = new Span("Yes");
            yesBtn.add(yes);

            Div yesDot = new Div();
            yesDot.addClassName("yesDot");

            yesBtn.add(yesDot);

            Div noBtn = new Div();
            noBtn.addClassName("noBtn");
            Span no = new Span("No");
            noBtn.add(no);

            Div noDot = new Div();
            noDot.addClassName("noDot");

            noBtn.add(noDot);

            sponsorPrompt.add(noBtn, yesBtn);

            noBtn.addClickListener(clickEvent -> {
                choosesToSponsor(false);
                boardContainer.remove(sponsorPrompt);
            });
            if (gameState.you.maxStages() < questDataPacket.stageCount) {
                System.out.println("MainView: Not enough cards to sponsor");
                yesBtn.addClickListener(click -> {
                    Notification error = Notification.show("You do not have enough cards to sponsor this quest");
                });
            } else {
                yesBtn.addClickListener(click -> {
                    choosesToSponsor(true);
                    boardContainer.remove(sponsorPrompt);
                });
            }

            boardContainer.add(sponsorPrompt);
        }
    }

    private void loadStory() {
        //&& gameState.storyCard.getType() == "Quest"
        if (gameState.storyCard != null) {
            boardContainer.removeAll();
            Image questImage = new Image(gameState.storyCard.getPathToImage(), "issue getting image");
            questImage.setMinWidth("442.5px");
            boardContainer.add(questImage);
            //getPlayerQuestCards();

        }
    }

    public void loadEndTurn() {
        turnInfoContainer.removeAll();
        if (gameState.you.getPlayerID() == gameState.currentPlayerID && gameState.readyToEndTurn == true) {
            Button endTurnButton = new Button("end turn");
            turnInfoContainer.add(endTurnButton);
            endTurnButton.addClickListener(clickEvent -> {
                mediator.endTurn();
                Broadcaster.broadcast("Handling end Turn Button");
            });
        }
        turnInfoContainer.add(new Paragraph("Player Turn: " + gameState.currentPlayerID));
    }

    private void getPlayerTourneyCards(){
        if (!tournamentMediator.isInTourney(gameState.you)) { //only players who agreed to participate can play cards for the quest
            return;
        }

        TournamentDataPacket tournamentDataPacket;
        if (gameState.storyDataPacket instanceof TournamentDataPacket) {
            tournamentDataPacket = (TournamentDataPacket) gameState.storyDataPacket;
        } else {
            //System.out.println("MainView: Attempt to load tourney when no quest available");
            return;
        }

        if(tournamentDataPacket.tourneyPhase == 2 && tournamentDataPacket.activePlayerID == gameState.you.getPlayerID()){
            getPlayerCards.removeAll();

            Button takeBackCardButton = new Button("Take Back Card");
            takeBackCardButton.getElement().addEventListener("click", e -> {
                gameState.you.drawAdventureCard(gameState.you.takeBackCard());

                playedCardsContainer.removeAll();
                for (int i = 0; i < gameState.you.getQuestStageCards().size(); i++) {
                    playedCardsContainer.add(new Image(gameState.you.getQuestStageCards().get(i).getPathToImage(), "issue getting image"));
                }

                loadMainPlayerDeck();
                Broadcaster.broadcast("default value");
                getPlayerTourneyCards();
            });

            Button playCardButton = new Button("Play Card");
            playCardButton.getElement().addEventListener("click", e -> {

                String str = playerDescription.getText();
                if (str.contains(">")) {
                    int index = Integer.parseInt(str.substring(str.lastIndexOf(">") + 2));
                    System.out.println(index + " " + gameState.you.getHand().get(index).getName());
                    boolean flag = false;
                    for(int i = 0; i < gameState.you.getQuestStageCards().size(); i++) {
                        if(gameState.you.getQuestStageCards().get(i).getName().equals(gameState.you.getHand().get(index).getName())){
                            flag = true;
                        }
                    }
                    if(gameState.you.getHand().get(index).getType().equals("Foe") || gameState.you.getHand().get(index).getType().equals("Test")){
                        createPrompt("CANNOT PLAY A FOE OR TEST IN A TOURNAMENT");
                        System.out.println("Player tried to play a foe or test");
                    } else if(flag){
                        createPrompt("CANNOT PLAY DUPLICATES");
                        //createPrompt letting them know the've already played this card
                        System.out.println("Player tried to play a card already played");
                    }
                    else{
                        gameState.you.addQuestingWeaponCard((WeaponCard) gameState.you.getHand().get(index));
                    }
                }

                playedCardsContainer.removeAll();
                for (int i = 0; i < gameState.you.getQuestStageCards().size(); i++) {
                    playedCardsContainer.add(new Image(gameState.you.getQuestStageCards().get(i).getPathToImage(), "issue getting image"));
                }
                loadMainPlayerDeck();
                Broadcaster.broadcast("default value");
                getPlayerTourneyCards();
            });


            Button submitCards = new Button("Submit Cards");
            submitCards.getElement().addEventListener("click", e -> {
                getPlayerCards.removeAll();
                tournamentMediator.submitTournamentCards();
            });

            getPlayerCards.add(playCardButton);

            if (gameState.you.getQuestStageCards().size() > 0) {
                getPlayerCards.add(takeBackCardButton);

            }
            getPlayerCards.add(submitCards);

            getPlayerCards.add(playedCardsContainer);
            boardContainer.add(getPlayerCards);
        }


    }


    /*public void getPlayerQuestCards() {
        //VerticalLayout getPlayerCards = new VerticalLayout();
        //HorizontalLayout playedCardsContainer = new HorizontalLayout();
        if (!questMediator.isQuesting(gameState.you)) { //only players who agreed to participate can play cards for the quest
            return;
        }

        QuestDataPacket questDataPacket;
        if (gameState.storyDataPacket instanceof QuestDataPacket) {
            questDataPacket = (QuestDataPacket) gameState.storyDataPacket;
        } else {
            //System.out.println("MainView: Attempt to load quest when no quest available");
            return;
        }

        if (questDataPacket.questPhase == 5 && questDataPacket.activePlayerID == gameState.you.getPlayerID())  {
            getPlayerCards.removeAll();

            Button takeBackCardButton = new Button("Take Back Card");
            takeBackCardButton.getElement().addEventListener("click", e -> {
                gameState.you.drawAdventureCard(gameState.you.takeBackCard());

                playedCardsContainer.removeAll();
                for (int i = 0; i < gameState.you.getQuestStageCards().size(); i++) {
                    playedCardsContainer.add(new Image(gameState.you.getQuestStageCards().get(i).getPathToImage(), "issue getting image"));
                }

                loadMainPlayerDeck();
                Broadcaster.broadcast("default value");
                getPlayerQuestCards();
            });

            Button playCardButton = new Button("Play Card");
            playCardButton.getElement().addEventListener("click", e -> {

                String str = playerDescription.getText();
                if (str.substring(str.length() - 3, str.length() - 2).equals(">")) {
                    int index = Integer.parseInt(str.substring(str.length() - 1));

                    if (gameState.you.getHand().get(index).getType().equals("Weapon")) {
                        gameState.you.addQuestingCard(gameState.you.getHand().get(index));
                    } else {
                        instructionContainer.removeAll();
                        selected = new H2("Only Weapon Cards can be used to beat a quest.");                       //this literally has no purpose right now, it's just to demonstrate the add functionality of SharedBoard
                        selected.getElement().setAttribute("class", "selected");
                        instructionContainer.add(selected);
                    }
                }

                playedCardsContainer.removeAll();
                for (int i = 0; i < gameState.you.getQuestStageCards().size(); i++) {
                    playedCardsContainer.add(new Image(gameState.you.getQuestStageCards().get(i).getPathToImage(), "issue getting image"));
                }
                loadMainPlayerDeck();
                Broadcaster.broadcast("default value");
                getPlayerQuestCards();
            });

            Button submitCards = new Button("Submit Cards");
            submitCards.getElement().addEventListener("click", e -> {
                getPlayerCards.removeAll();
                questMediator.submitQuestStageCards();
            });


            getPlayerCards.add(new Paragraph("Cards for Stage: "));
            getPlayerCards.add(playCardButton);

            if (gameState.you.getQuestStageCards().size() > 0) {
                getPlayerCards.add(takeBackCardButton);

            }
            getPlayerCards.add(submitCards);

            getPlayerCards.add(playedCardsContainer);
            boardContainer.add(getPlayerCards);
        }
    }*/

    private void loadSubPlayer() {
        subPlayersContainer.removeAll();
        for (int i = 0; i < gameState.playerCount; i++) {
            playerCount++;
            if (gameState.you.getPlayerID() != i) {
                HorizontalLayout subPlayerRow = new HorizontalLayout();
                setSpacing(false);
                Paragraph subPlayerInfo = new Paragraph("player: " + i + " Rank: " + gameState.playerRanks.get(i) + " Shields: " + gameState.playerShields.get(i) + " Hand Size: " + gameState.handSizes.get(i));
                Image cardBack = new Image("images/default.png", "default placeholder");
                cardBack.setWidth("75px");
                cardBack.getElement().setAttribute("class", "subPlayerDeck" + i);
                subPlayerRow.add(cardBack);
                subPlayerRow.add(subPlayerInfo);
                subPlayersContainer.add(subPlayerRow);
            }
        }
    }

    private void loadPlayerCards() {
        loadMainPlayerDeck();
        setSpacing(false);
        DrawDiscardContainer = new HorizontalLayout(playerDescription);

        // ADD THESE LINES OF CODE BACK IF YOU NEED TO DO TESTING WITH DRAWING
//        Button drawButton1 = new Button("Draw");
//        DrawDiscardContainer = new HorizontalLayout(drawButton1, playerDescription); //
//
//        // THIS WILL BE REPLACED LATER BUT JUST LEAVE IT HERE FOR NOW, ITS JUST TO SHOWCASE FOR THE WEEKLY CHECK-IN
//        // Draw button event Listener type "click" for testing, will probably change to an end button event listener
//        drawButton1.addClickListener(clickEvent -> {
//            gameState.you.drawAdventureCard();
//            Broadcaster.broadcast("default value");
//            if (gameState.you.handSize() >= 13) {
//                selected.setText("You've exceeded your hand limit, please discard down to 12 cards");
//
//                //Create discard button
//                Button discardButton = new Button("Discard Card");
//                discardButton.getElement().addEventListener("click", e -> {
//                    String str = playerDescription.getText();
//                    if (str.contains(">")) {
//                        int index = Integer.parseInt(str.substring(str.lastIndexOf(">") + 2));
//                        gameState.you.discard(gameState.you.getHand().get(index));
//                    }
//                    loadMainPlayerDeck();
//                    Broadcaster.broadcast("default value");
//
//                    //Removes Discard button when the user has the proper amount of cards
//                    if (gameState.you.handSize() <= 12) {
//                        DrawDiscardContainer.remove(discardButton);
//                        selected.setText("Instructions for user (Please draw or please you over drew please discard, etc..)");
//                    }
//                });
//                DrawDiscardContainer.add(discardButton);
//            }
//        });
        DrawDiscardContainer.setAlignItems(FlexComponent.Alignment.BASELINE);
        add(DrawDiscardContainer); //(Row 3) Adding draw, player hand count, and discard card
        add(horizontalHand1);
    }

    private void loadMainPlayerDeck() {
        cardInfoContainer.removeAll();
        if (gameState != null && gameState.you != null) {
            horizontalHand1.removeAll();

            for (int i = 0; i < gameState.you.handSize(); i++) {
                int cardIndex = i;
                Image cardImage = new Image(gameState.you.getHand().get(i).getPathToImage(), "issue getting image");
                cardImage.setWidth("75px");
                // creating dynamic class names which will be used for their respective click listeners for selecting, playing and discarding
                //String className = "p" + gameState.you.getPlayerID() + "I" + cardIndex;
                cardImage.getElement().setAttribute("class", "cardHover");
                horizontalHand1.add(cardImage);
                playerDescription.setText(getPlayerInfo(gameState.you.getPlayerID(), gameState.you.getRank().getName(), gameState.you.getShields(), cardIndex + 1));
                cardImage.getElement().addEventListener("click", e -> {
                    playerDescription.setText(getPlayerInfo(gameState.you.getPlayerID(), gameState.you.getRank().getName(), gameState.you.getShields(), gameState.you.getHand().size()) + " - Card Selected => " + cardIndex);
                    displaySelectedCard(gameState.you.getHand().get(cardIndex), cardIndex);
                });
            }
        }
    }

    private void loadKingCall() {

        //System.out.println("TEST");
        EventDataPacket eventDataPacket;
        if (mediator.getStoryDataPacket() instanceof EventDataPacket) {
            eventDataPacket = (EventDataPacket) mediator.getStoryDataPacket();
        } else {
            //System.out.println("MainView: Attempt to load event when no quest available");
            return;
        }

        if (eventDataPacket.kingCallHighestRanked.contains(gameState.you)) {
            //load for discard
            //Create discard button
            boolean hasWeapon = false;

            for (int i = 0; i < gameState.you.getHand().size(); i++) {
                if (gameState.you.getHand().get(i).getType().equals("Weapon")) {
                    hasWeapon = true;
                }
            }

            Div discardPrompt = new Div();
            discardPrompt.setClassName("PromptDiv");

            Span discardSpan = new Span();
            discardSpan.addClassName("promptSpan");
            discardPrompt.add(discardSpan);

            Div discardBtn = new Div();
            discardBtn.addClassName("btn");
            Span yes = new Span("Discard");
            discardBtn.add(yes);

            Div discardDot = new Div();
            discardDot.addClassName("dot");

            discardBtn.add(discardDot);

            discardPrompt.add(discardBtn);

            if (hasWeapon == true) {
                discardSpan.setText("Discard a Weapon");
                discardBtn.addClickListener(clickEvent -> {
                    String str = playerDescription.getText();
                    if (str.contains(">")) {
                        int index = Integer.parseInt(str.substring(str.lastIndexOf(">") + 2));
                        if (gameState.you.getHand().get(index).getType() == "Weapon") {
                            gameState.you.discard(gameState.you.getHand().get(index));
                            boardContainer.remove(discardPrompt);

                            mediator.kingsCallDiscarded();
                            loadMainPlayerDeck();
                            return;
                        }else{
                            createPrompt("PLEASE SELECT A WEAPON CARD");
                        }
                    }
                });
            } else {
                discardSpan.setText("Discard 2 Foes");
                discardBtn.addClickListener(clickEvent -> {
                    String str = playerDescription.getText();
                    if (str.contains(">")) {
                        int index = Integer.parseInt(str.substring(str.lastIndexOf(">") + 2));
                        if (gameState.you.getHand().get(index).getType() == "Foe") {
                            gameState.you.discard(gameState.you.getHand().get(index));
                            if (flag == false) {
                                loadMainPlayerDeck();
                                setflag();

                                createPrompt("PLEASE DISCARD ONE MORE FOE");
                            }else{
                                boardContainer.remove(discardPrompt);
                                //now can end turn
                                mediator.kingsCallDiscarded();
                                loadMainPlayerDeck();
                                return;
                            }
                        }else{
                            createPrompt("PLEASE SELECT A FOE CARD");
                        }
                    }
                });
            }
            boardContainer.add(discardPrompt);
        }
        loadMainPlayerDeck();
    }

    private void setflag() {
        flag = true;
    }

    private String getIdFromUrl() {
        Location location = UI.getCurrent().getInternals().getActiveViewLocation();
        String path = location.getPath();
        String segments[] = path.split("/");
        String playerIdStr = segments[segments.length - 1];
        return playerIdStr;
    }

    private void choosesToSponsor(Boolean choice) {
        if (choice == true) {
            //mediator is told that this player is the sponsor
            mediator.confirmSponsor();
            //sponsorStageSetup.add();
        }
        if (choice == false) {
            //mediator is told that this player is not the sponsor (asks the next player)
            mediator.refuseSponsor();
        }
    }


    private void choosesToTourney(Boolean choice) {
        tournamentMediator.submitInviteDecision(choice);
    }


    //load quest stages && sponsor stage setup

    private void loadQuestStages() {
        // (1) loadQuestStages variable setup START

        QuestDataPacket questDataPacket; //This Is used to get the quest phase
        if (gameState.storyDataPacket instanceof QuestDataPacket) {
            questDataPacket = (QuestDataPacket) gameState.storyDataPacket;
        } else {
            //System.out.println("MainView: Attempt to load quest when no quest available");
            return;
        }
        System.out.println("sponsorid is currently: " + questDataPacket.sponsorID);
        //checking if there is a sponsor, this gets reset apporpriately when we're not questing.
        if (questDataPacket.sponsorID >= 0) {
            for (int i = 0; i < questDataPacket.getStageCount(); i++) {
                int stageCounter = i;
                VerticalLayout stageContainer = new VerticalLayout();
                Image stageImage = new Image("images/default.png", "default placeholder");
                stageImage.getElement().setAttribute("class", "stageDeck" + i);
                stageContainer.add(stageImage);
                Paragraph stageInfo = new Paragraph("Stage " + i + " power: 0");
                stageContainer.add(stageInfo);

                // (1) loadQuestStages variable setup END

                // (2) print stage # and stage power # for each stage appropriately
                if (gameState.you.getPlayerID() == questDataPacket.sponsorID && questDataPacket.questPhase > 2) {
                    stageInfo.setText("stage: " + stageCounter + " power: " + stageList.get(stageCounter).calcFoePower());


                    // if foe card
                    if (stageList.get(i).getMainCard() instanceof FoeCard) {
                        stageImage.setSrc(((FoeCard) stageList.get(i).getMainCard()).getPathToImage());
                    } else {
                        stageImage.setSrc(((TestCard) stageList.get(i).getMainCard()).getPathToImage());
                    }

                    //if test card
                } else if (gameState.you.getPlayerID() != questDataPacket.sponsorID && questDataPacket.questPhase > 2 && stageCounter < questDataPacket.currentStage) {
                    stageInfo.setText("stage: " + stageCounter + " power: " + stageList.get(stageCounter).calcFoePower());
                    // if foe card
                    if (stageList.get(i).getMainCard() instanceof FoeCard) {
                        stageImage.setSrc(((FoeCard) stageList.get(i).getMainCard()).getPathToImage());
                    } else {
                        stageImage.setSrc(((TestCard) stageList.get(i).getMainCard()).getPathToImage());
                    }
                } else if (gameState.you.getPlayerID() != questDataPacket.sponsorID && questDataPacket.questPhase > 2 && stageCounter == questDataPacket.currentStage) {
                    if (stageList.get(i).getMainCard() instanceof TestCard) {
                        stageImage.setSrc(((TestCard) stageList.get(i).getMainCard()).getPathToImage());
                        stageInfo.setText("Test Card, Current Bid: " + questDataPacket.currentBid);
                    }

                } else {
                    stageInfo.setText("stage: " + stageCounter + " power: " + getDeckPower(sponsorStageSetup.get(stageCounter)));
                }

                // (2) print stage # and stage power # for each stage appropriately END

                // (3) View List of Cards of each stage by clicking on it START
                stageImage.getElement().addEventListener("click", e -> {
                    cardInfoContainer.removeAll();
                    cardInfoContainer.add(new Paragraph("Stage " + stageCounter + " info"));
                    // Handling stage piles for sponsor
                    if (gameState.you.getPlayerID() == questDataPacket.sponsorID && questDataPacket.questPhase > 2) {
                        //add condition for tests


                        if (stageList.get(stageCounter).getMainCard() instanceof FoeCard) {
                            cardInfoContainer.add(((FoeCard) stageList.get(stageCounter).getMainCard()).toString());
                        } else {
                            cardInfoContainer.add(((TestCard) stageList.get(stageCounter).getMainCard()).toString());
                        }


                        for (int j = 0; j < stageList.get(stageCounter).getWeapons().size(); j++) {
                            Paragraph listCards = new Paragraph(stageList.get(stageCounter).getWeapons().get(j).toString());
                            cardInfoContainer.add(listCards);
                        }


                        // Handling stage piles for players
                    } else {
                        if (stageCounter < questDataPacket.currentStage) {

                            if (stageList.get(stageCounter).getMainCard() instanceof FoeCard) {
                                cardInfoContainer.add(((FoeCard) stageList.get(stageCounter).getMainCard()).toString());
                            } else {
                                cardInfoContainer.add(((TestCard) stageList.get(stageCounter).getMainCard()).toString());
                            }

                            for (int j = 0; j < stageList.get(stageCounter).getWeapons().size(); j++) {
                                Paragraph listCards = new Paragraph(stageList.get(stageCounter).getWeapons().get(j).toString());
                                cardInfoContainer.add(listCards);
                            }
                        }

                    }
                });

                // (3) View List of Cards of each stage by clicking on it END

                // (4) Chosen Sponsor sets up stage order START
                if (gameState.you.getPlayerID() == questDataPacket.sponsorID && questDataPacket.questPhase == 2) {
                    if (sponsorStageSetup.get(stageCounter).size() > 0) {
                        if (sponsorStageSetup.get(stageCounter).get(0).getType().equals("Foe")) {
                            stageImage.setSrc(sponsorStageSetup.get(stageCounter).get(0).getPathToImage());
                        }
                        if (sponsorStageSetup.get(stageCounter).get(0).getType().equals("Test")) {
                            stageImage.setSrc(sponsorStageSetup.get(stageCounter).get(0).getPathToImage());
                        }
                    }
                    //play on stage button
                    Button playOnStageButton = new Button("play on stage");
                    stageContainer.add(playOnStageButton);
                    playOnStageButton.addClickListener(clickEvent -> {
                        String str = playerDescription.getText();
                        if (str.contains(">")) {
                            int index = Integer.parseInt(str.substring(str.lastIndexOf(">") + 2));

                            //adding type foe to quest stage - only allows the player to put in one foe card for each stage
                            if (gameState.you.getHand().get(index).getType().equals("Foe")) {
                                if (sponsorStageSetup.get(stageCounter).size() > 0) {
                                    if (!sponsorStageSetup.get(stageCounter).get(0).getType().equals("Foe") && !sponsorStageSetup.get(stageCounter).get(0).getType().equals("Test")) {
                                        sponsorStageSetup.get(stageCounter).add(0, gameState.you.getHand().get(index));
                                        gameState.you.getHand().remove(index);
                                        Broadcaster.broadcast("played card on stage");

                                    }
                                } else {
                                    sponsorStageSetup.get(stageCounter).add(0, gameState.you.getHand().get(index));
                                    //System.out.println("stage " + stageCounter + " => " + sponsorStageSetup);
                                    gameState.you.getHand().remove(index);
                                    Broadcaster.broadcast("played card on stage");
                                }
                                //adding type weapon to quest stage - only allows the player to put in one of each weapon for each stage
                            } else if (gameState.you.getHand().get(index).getType().equals("Weapon")) {
                                boolean dupWeapon = false;
                                boolean testExists = false;
                                for (int j = 0; j < sponsorStageSetup.get(stageCounter).size(); j++) {

                                    if (sponsorStageSetup.get(stageCounter).get(j).toString().equals(gameState.you.getHand().get(index).toString())) {
                                        dupWeapon = true;
                                    }

                                    if (sponsorStageSetup.get(stageCounter).get(j).getType().equals("Test")) {
                                        testExists = true;
                                    }
                                }
                                if (dupWeapon == false && testExists == false) {
                                    sponsorStageSetup.get(stageCounter).add(gameState.you.getHand().get(index));
                                    //System.out.println("stage " + stageCounter + " weapon added => " + sponsorStageSetup);
                                    gameState.you.getHand().remove(index);
                                    Broadcaster.broadcast("played card on stage");
                                }
                                //check for duplicate tests
                            } else if (gameState.you.getHand().get(index).getType().equals("Test")) {

                                boolean dupFoe = false;
                                for (int j = 0; j < sponsorStageSetup.size(); j++) {
                                    // int stageStepThrough=j;
                                    if(!sponsorStageSetup.get(j).isEmpty()){
                                        if (sponsorStageSetup.get(j).get(0).getType().equals(gameState.you.getHand().get(index).getType())) {
                                            dupFoe = true;
                                        }
                                    }
                                }

                                    if (dupFoe == false) {
                                        sponsorStageSetup.get(stageCounter).add(gameState.you.getHand().get(index));
                                        //System.out.println("stage " + stageCounter + " weapon added => " + sponsorStageSetup);
                                        gameState.you.getHand().remove(index);
                                        Broadcaster.broadcast("played test card on stage");

                                    }
                            }
                            //adding tests Will implement this later (not required to do for sprint 3)
                        }
                    });
                    Button clearStageButton = new Button("clear stage " + i);
                    stageContainer.add(clearStageButton);
                    clearStageButton.addClickListener(clickEvent -> {
                        int stageSize = sponsorStageSetup.get(stageCounter).size();
                        for (int j = stageSize - 1; j >= 0; j--) {
                            gameState.you.getHand().add(sponsorStageSetup.get(stageCounter).get(j));
                            sponsorStageSetup.get(stageCounter).remove(j);
                        }
                        Broadcaster.broadcast("Sponsor Cleared stage" + stageContainer + " cards back to their hand");
                    });
                }
                // (4) Chosen Sponsor sets up stage order END
                boardContainer.add(stageContainer);
            }

            // (5) Sponsor quest stage setup confirm button START
            if (gameState.you.getPlayerID() == questDataPacket.getSponsorId()) {
                Button confirmAllStagesButton = new Button("Confirm All Stages");
                if (questDataPacket.questPhase == 2) {
                    turnInfoContainer.add(confirmAllStagesButton);
                }
                //This does the error checking to see if the stage is in the correct order or not
                confirmAllStagesButton.addClickListener(clickEvent -> {
                    boolean isStageOrderValid = true;
                    isStageOrderValid = checkStageOrderValid();
                    //If it is valid go to participants page.
                    if (isStageOrderValid == true) {
                        System.out.println("SUCCESS, NOW MOVE TO FIND PARTICIPANTS STAGE");
                        System.out.println(sponsorStageSetup);
                        questMediator.submitQuestStages(sponsorStageSetup);
                        turnInfoContainer.remove(confirmAllStagesButton);
                        Broadcaster.broadcast("confirm stage setup");
                        //may need to change this
                        //sponsorStageSetup=null;
                    }
                });
            }

            // (5) Sponsor quest stage setup confirm button END
        }
    }

    private void choosesToQuest(Boolean choice) {
        questMediator.submitInviteDecision(choice);
    }

    private void getPlayerQuestCards() {

        //setup (1) START
        if (!questMediator.isQuesting(gameState.you)) { //only players who agreed to participate can play cards for the quest
            return;
        }

        QuestDataPacket questDataPacket;
        if (gameState.storyDataPacket instanceof QuestDataPacket) {
            questDataPacket = (QuestDataPacket) gameState.storyDataPacket;
        } else {
            System.out.println("MainView: Attempt to load quest when no quest available");
            return;
        }
        //setup (1) END

        //FoeStage (2) START
        if (questDataPacket.questPhase == 5 && questDataPacket.activePlayerID == gameState.you.getPlayerID()) {
            getPlayerCards.removeAll();

            Paragraph stagePower = new Paragraph("Stage Power: " + gameState.you.getCurrentBP());

            Button takeBackCardButton = new Button("Take Back Card");
            takeBackCardButton.getElement().addEventListener("click", e -> {
                gameState.you.drawAdventureCard(gameState.you.takeBackCard());

                playedCardsContainer.removeAll();
                for (int i = 0; i < gameState.you.getQuestStageCards().size(); i++) {
                    playedCardsContainer.add(new Image(gameState.you.getQuestStageCards().get(i).getPathToImage(), "issue getting image"));
                }

                //getPlayerCards.remove(stagePower);
                stagePower.setText("Stage Power: " + gameState.you.getCurrentBP());
                getPlayerCards.add(stagePower);
                loadMainPlayerDeck();
                Broadcaster.broadcast("default value");
                getPlayerQuestCards();
            });

            Button playCardButton = new Button("Play Card");
            playCardButton.getElement().addEventListener("click", e -> {

                String str = playerDescription.getText();
                if (str.contains(">")) {
                    int index = Integer.parseInt(str.substring(str.lastIndexOf(">") + 2));
                    // System.out.println

                    if (gameState.you.getHand().get(index).getType().equals("Weapon") && !gameState.you.playedWeapon(gameState.you.getHand().get(index))) {

                        gameState.you.addQuestingWeaponCard((WeaponCard) gameState.you.getHand().get(index));
                    } else if (gameState.you.playedWeapon(gameState.you.getHand().get(index))) {
                        instructionContainer.removeAll();
                        instructionText = new H2("You cannot play duplicate weapons on the same quest stage.");      //this literally has no purpose right now, it's just to demonstrate the add functionality of SharedBoard
                        instructionText.getElement().setAttribute("class", "selected");
                        instructionContainer.add(instructionText);
                    } else {
                        instructionContainer.removeAll();
                        instructionText = new H2("Quests Of The Round Table");      //this literally has no purpose right now, it's just to demonstrate the add functionality of SharedBoard
                        instructionText.getElement().setAttribute("class", "selected");
                        instructionContainer.add(instructionText);
                    }
                }

                playedCardsContainer.removeAll();
                for (int i = 0; i < gameState.you.getQuestStageCards().size(); i++) {
                    playedCardsContainer.add(new Image(gameState.you.getQuestStageCards().get(i).getPathToImage(), "issue getting image"));
                }
                //getPlayerCards.remove(stagePower);
                stagePower.setText("Stage Power: " + gameState.you.getCurrentBP());
                getPlayerCards.add(stagePower);
                loadMainPlayerDeck();
                Broadcaster.broadcast("default value");
                getPlayerQuestCards();
            });

            Button submitCards = new Button("Submit Cards");
            submitCards.getElement().addEventListener("click", e -> {
                getPlayerCards.removeAll();
                questMediator.submitQuestStageCards();
                playedCardsContainer.removeAll();
            });
            getPlayerCards.add(new Paragraph("Cards for Stage: "));
            getPlayerCards.add(playCardButton);

            if (gameState.you.getQuestStageCards().size() > 0) {
                getPlayerCards.add(takeBackCardButton);

            }
            getPlayerCards.add(submitCards);

            getPlayerCards.add(playedCardsContainer);
            getPlayerCards.add(stagePower);
            boardContainer.add(getPlayerCards);
        }

        //FoeStage (2) END
    }


    private void askToBid() {
        // Variable Setup START (1)
        QuestDataPacket questDataPacket;
        if (gameState.storyDataPacket instanceof QuestDataPacket) {
            questDataPacket = (QuestDataPacket) gameState.storyDataPacket;
        } else {
            System.out.println("MainView: Attempt to load quest when no quest available");
            return;
        }
        // Variable Setup END (2)


        if (questDataPacket.questPhase == 6 && questDataPacket.activePlayerID == gameState.you.getPlayerID()) {

            System.out.println("ASK to BID" + getIdFromUrl() + " current stage variable: " + questDataPacket.currentStage);
            // Create Prompt for bidding START (2)
            Div questPrompt = new Div();
            questPrompt.setMinHeight(10, Unit.EM);
            questPrompt.setMinWidth(20, Unit.EM);
            questPrompt.setClassName("PromptDiv");

            Span questSpan = new Span("Would you like to up the Bid to: " + ((int) questDataPacket.currentBid + 1) + " cards");
            questSpan.addClassName("promptSpan");
            questPrompt.add(questSpan);

            Div yesBtn = new Div();
            yesBtn.addClassName("yesBtn");
            Span yes = new Span("Yes");
            yesBtn.add(yes);

            Div yesDot = new Div();
            yesDot.addClassName("yesDot");

            yesBtn.add(yesDot);

            Div noBtn = new Div();
            noBtn.addClassName("noBtn");
            Span no = new Span("No");
            noBtn.add(no);

            Div noDot = new Div();
            noDot.addClassName("noDot");

            noBtn.add(noDot);

            questPrompt.add(noBtn, yesBtn);

            noBtn.addClickListener(clickEvent -> {
                boardContainer.remove(questPrompt);
                stageList.get(questDataPacket.currentStage).submitBidDecision(false);
            });
            yesBtn.addClickListener(click -> {
                boardContainer.remove(questPrompt);
                //questDataPacket.currentBid++;
                stageList.get(questDataPacket.currentStage).submitBidDecision(true);
            });
            boardContainer.add(questPrompt);
        }
        // Create Prompt for bidding END (2)
    }

    //Function That Handles quest phase 7 (Player discard # cards from test)
    private void testBidPayment() {
        // Variable Setup START (1)
        QuestDataPacket questDataPacket;
        if (gameState.storyDataPacket instanceof QuestDataPacket) {
            questDataPacket = (QuestDataPacket) gameState.storyDataPacket;
        } else {
            System.out.println("MainView: Attempt to load quest when no quest available");
            return;
        }
        // Variable Setup END (1)
        System.out.println("testBidPayment: " + " testCardDiscardCount: " + questDataPacket.testDiscardCount + " currentBid: " + questDataPacket.currentBid);
        if (questDataPacket.questPhase == 7 && questDataPacket.activePlayerID == gameState.you.getPlayerID() && questDataPacket.testDiscardCount < questDataPacket.currentBid) {

            // Create Prompt for bidding START (2)
            Div discardPrompt = new Div();
            discardPrompt.setClassName("PromptDiv");
            Span discardSpan = new Span();
            discardSpan.addClassName("promptSpan");
            discardPrompt.add(discardSpan);

            Div discardBtn = new Div();
            discardBtn.addClassName("btn");
            Span yes = new Span("Discard");
            discardBtn.add(yes);

            Div discardDot = new Div();
            discardDot.addClassName("dot");

            discardBtn.add(discardDot);

            discardPrompt.add(discardBtn);

            discardSpan.setText("Discard " + (questDataPacket.currentBid - questDataPacket.testDiscardCount) + " cards ");

            // Create Prompt for bidding END (2)

            // Handles Discard card, uses questDataPacket.testDiscardCount and  questDataPacket.currentBid to keep track of the needed cards ti discard (3)
            discardBtn.addClickListener(clickEvent -> {
                String str = playerDescription.getText();
                if (str.contains(">")) {
                    int index = Integer.parseInt(str.substring(str.lastIndexOf(">") + 2));

                    gameState.you.discard(gameState.you.getHand().get(index));
                    questDataPacket.testDiscardCount++;

                    boardContainer.remove(discardPrompt);

                    //If Player has discarded all the cards necessary go to the next stage
                    if (questDataPacket.testDiscardCount == questDataPacket.currentBid) {
                        System.out.println("testBidPayment NEXT STAGE");
                        questDataPacket.testDiscardCount = 0;
                        stageList.get(questDataPacket.currentStage).submitTestDiscard();
                    }
                    Broadcaster.broadcast("submitting test card discards");


                }
            });
            // Handles Discard card, uses questDataPacket.testDiscardCount and  questDataPacket.currentBid to keep track of the needed cards ti discard (3)

            boardContainer.add(discardPrompt);
        }

    }

    private void displayShields() {
        int g = gameState.you.getShields();
        for (int i = 0; i < g; i++) {

        }
    }

    private void createPrompt(String text){
        Span notiText = new Span(text);
        notiText.setClassName("notiText");
        Notification prompt = new Notification();
        prompt.add(notiText);
        prompt.setPosition(Notification.Position.MIDDLE);
        prompt.setDuration(3000);
        prompt.open();
        prompt.show("");
        prompt.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        add(prompt);
    }
}
