package org.vaadin;

import com.vaadin.flow.component.UI;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Initializer {

    @Autowired
    private Mediator mediator;
    private final ArrayList<Player> players = new ArrayList<>();


    @Autowired
    private ConfigurableApplicationContext context;

    private boolean gameHasStarted = false;
    public boolean hasGameStarted(){
        return gameHasStarted;
    }


    public Player joinGame(String name){
        System.out.println(players.size());
        if (players.size() < 4) {

            Player newPlayer = context.getBean(Player.class, name, players.size());

            players.add(newPlayer);
            newPlayer.drawAdvCards(12);

            System.out.println(name + " has joined the game as player "+ players.size());
            return players.get(players.size()-1);
        }

        System.out.println("Failed attempt to join full game");
        return null;
    }

    public Player getPlayer(int pid){
        return players.get(pid);
    }

    public ArrayList<String> getPlayerNames(){
        ArrayList<String> output = new ArrayList<>();
        for(Player player : players){
            output.add(player.getName());
        }
        return output;
    }

    public int startGame(){
        for(Player player : players){
            if(!player.isReady()){
                System.out.println("Cannot start game, player "+player.getPlayerID()+" has not readied up.");

                return -1;
            }
        }

        if(players.size() > 1 && gameHasStarted == false){
            System.out.println("Starting " + players.size() + " player game.");
            gameHasStarted = true;
            mediator.addPlayers(players);
            System.out.println(players.size() + "TEST");
            mediator.startTurn(players.get(0));
            return 0;
        }

        if(players.size() <= 1){
            System.out.println("Cannot start game without at least 2 players");
            return -2;
        }
        return -3;
    }

}
