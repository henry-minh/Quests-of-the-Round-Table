package org.vaadin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {



    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);


        initPlayers(context);

    }

    private static void initPlayers(ConfigurableApplicationContext context){
        Initializer init = context.getBean(Initializer.class);
        init.joinGame("Player1");
        // this should fail, not enough players
        init.startGame();
        init.joinGame("Player2");
        init.joinGame("Player3");
        init.joinGame("Player4");
        //player 5 should fail to join
       // init.joinGame("Player5");

        init.getPlayer(0).readyUp();
        init.getPlayer(1).readyUp();
        init.getPlayer(2).readyUp();
        init.getPlayer(3).readyUp();
        init.startGame();
    }

}
