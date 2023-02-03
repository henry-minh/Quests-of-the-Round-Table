package org.vaadin.card;

public class TournamentCard extends Card{

    TournamentInfo info;

    public TournamentCard(String n, TournamentInfo ti){
        type = "Tournament";
        name = n;
        info = ti;
        path = ti.getPathToImage();
    }

    public int getExtraShields(){ return info.getExtraShields();}
}
