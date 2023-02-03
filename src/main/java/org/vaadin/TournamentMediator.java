package org.vaadin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.card.TournamentCard;

import java.util.ArrayList;

@Component
public class TournamentMediator{

    @Autowired
    private Mediator mediator;

    private TournamentCard tournamentCard;
    private final ArrayList<Player> tournamentPlayers = new ArrayList<>();
    private TournamentDataPacket tournamentDataPacket;
    private Player activePlayer;


    public void init(TournamentCard c, ArrayList<Player> players, Player firstPlayer, TournamentDataPacket packet){
        tournamentCard = c;
        tournamentDataPacket = packet;
        //these loops rearrange the players array into a new array for the tournament,
        // in turn order starting from the
        for(int i = players.indexOf(firstPlayer); i < players.size(); i++){
            tournamentPlayers.add(players.get(i));
        }
        for(int i = 0; i < players.indexOf(firstPlayer); i++){
            tournamentPlayers.add(players.get(i));
        }

        tournamentDataPacket.tourneyPhase = 1;
        inviteToTournament(tournamentPlayers.get(0));
    }

    private void inviteToTournament(Player p){
        activePlayer = p;
        tournamentDataPacket.activePlayerID = p.getPlayerID();
        Broadcaster.broadcast("Invite Player "+p.getPlayerID()+" to tournament");
    }

    public void submitInviteDecision(boolean decision){
        boolean lastInvite = activePlayer == tournamentPlayers.get(tournamentPlayers.size() - 1);

        if(decision) {
            if(lastInvite){
                startRequestTournamentCards();
            }
            else{
                inviteToTournament(tournamentPlayers.get(tournamentPlayers.indexOf(activePlayer) + 1));
            }
        }
        else{
            int i = tournamentPlayers.indexOf(activePlayer);
            tournamentPlayers.remove(activePlayer);
            if(lastInvite){
                if(tournamentPlayers.size()>0){
                    startRequestTournamentCards();
                }
                else{
                    wrapUp(tournamentPlayers);
                }
            }
            else{
                inviteToTournament(tournamentPlayers.get(i));
            }
        }
        Broadcaster.broadcast("Asking next person to join tourney");
    }

    private void startRequestTournamentCards(){
        tournamentDataPacket.tourneyPhase = 2;
        for(Player player: tournamentPlayers){
            player.drawAdventureCard();
        }
        requestTournamentCards(tournamentPlayers.get(0));
    }
    private void requestTournamentCards(Player p){
        activePlayer = p;
        tournamentDataPacket.activePlayerID = p.getPlayerID();
        Broadcaster.broadcast("Player "+p.getPlayerID()+" to play tournament cards");
    }

    public void submitTournamentCards(){
        //play cards - currently allies might not be "moved" correctly
        activePlayer.playCards();

        //go next
        boolean lastPlayer = activePlayer == tournamentPlayers.get(tournamentPlayers.size()-1);
        if(lastPlayer){
            resolve();
        }
        else{
            requestTournamentCards(tournamentPlayers.get(tournamentPlayers.indexOf(activePlayer)+1));
        }
    }

    private void resolve(){
        int maxBP = 0;
        ArrayList<Player> winners = new ArrayList<>();
        for(Player player : tournamentPlayers){
            if(player.getCurrentBP() > maxBP){
                maxBP = player.getCurrentBP();
                winners.clear();
                winners.add(player);
            }
            else if(player.getCurrentBP() == maxBP){
                winners.add(player);
            }
        }
        //add code for ties here later
        wrapUp(winners);
    }

    private void wrapUp(ArrayList<Player> winners) {
        int rewardNum = tournamentPlayers.size() + tournamentCard.getExtraShields();
        for(Player winner: winners){
            winner.setShields(winner.getShields()+rewardNum);
        }
        tournamentPlayers.clear();
        mediator.setReadyToEndTurn(true);
        Broadcaster.broadcast("Tournament Over");
    }

    public boolean isInTourney(Player p) {
        for (Player t : tournamentPlayers) {
            if (t.getPlayerID() == p.getPlayerID()) {
                return true;
            }
        }
        return false;
    }

}
