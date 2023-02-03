package org.vaadin;

import org.vaadin.card.Card;

import java.util.ArrayList;

public class GameDataPacket {
    int currentPlayerID;
    int advDeckCardCount;
    ArrayList<Card> advDiscard;
    Player you;
    ArrayList<Integer> handSizes;
    ArrayList<Integer> playerShields;
    ArrayList<Integer> winnerIDs;
    ArrayList<String> playerRanks;
    Card storyCard;
    int playerCount;
    StoryDataPacket storyDataPacket;
    boolean readyToEndTurn;


    public GameDataPacket(int cpid, int aDeck, ArrayList<Card> aDisc, Player p,
                          ArrayList<Integer> hs, ArrayList<Integer> ps, ArrayList<String> pr, ArrayList<Integer> winnerIDs, boolean readyToEndTurn, Card story, int pc, StoryDataPacket event){

        currentPlayerID = cpid;
        advDeckCardCount = aDeck;
        advDiscard = aDisc;
        you = p;
        handSizes = hs;
        playerRanks = pr;
        playerShields = ps;
        storyCard = story;
        playerCount = pc;
        storyDataPacket = event;
        this.winnerIDs = winnerIDs;
        this.readyToEndTurn = readyToEndTurn;

    }
}
