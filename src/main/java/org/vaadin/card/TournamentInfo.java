package org.vaadin.card;

public class TournamentInfo {

    private String pathToImage;
    private int extraShields;

    public TournamentInfo(String p, int extra){
        pathToImage = p;
        extraShields = extra;
    }

    public String getPathToImage(){
            return pathToImage;
        }
    public int getExtraShields(){ return extraShields;}

}
