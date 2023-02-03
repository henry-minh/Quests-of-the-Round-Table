package org.vaadin.page;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.Initializer;
import org.vaadin.Player;


@Route(value = "/Player/:id([0-3]*)")
@PageTitle("PlayerPage")
@CssImport("./styles/LandingPageTheme.css")
@Push
public class PlayerPage extends VerticalLayout {
    @Autowired
    private Initializer init;

    public PlayerPage(){
        this.setAlignItems(Alignment.CENTER);
        Div containerReady = new Div();
        containerReady.addClassName("container");
        containerReady.addClickListener(click -> readyPlayer());

        Div readyBtn = new Div();
        readyBtn.addClassName("btn");
        readyBtn.add(new Span("Ready"));

        Div dotReady = new Div();
        dotReady.addClassName("dot");

        Div containerJoin = new Div();
        containerJoin.addClassName("container");
        containerJoin.addClickListener(click -> joinGame());

        Div joinBtn = new Div();
        joinBtn.addClassName("btn");
        joinBtn.add(new Span("Join"));

        Div dotJoin = new Div();
        dotJoin.addClassName("dot");

        containerReady.add(readyBtn);
        readyBtn.add(dotReady);

        containerJoin.add(joinBtn);
        joinBtn.add(dotJoin);

        add(containerReady,containerJoin);
    }

    private void joinGame(){
        if(init.startGame() == -1){
            createPrompt("Not all players are ready yet");
        }
        if(init.startGame() == -2){
            createPrompt("Not enough players have joined");
        }

        if(init.hasGameStarted()){
            int playerId = Integer.parseInt(getIdFromUrl());
            System.out.println(playerId);
            UI.getCurrent().navigate("gameStart/" + playerId);
        }
    }

    private void readyPlayer() {
        int playerId = Integer.parseInt(getIdFromUrl());
        System.out.println(playerId);

        Player player = init.getPlayer(playerId);
        player.readyUp();
    }

    private String getIdFromUrl(){
        Location location = UI.getCurrent().getInternals().getActiveViewLocation();
        String path = location.getPath();
        String segments[] = path.split("/");
        String playerIdStr = segments[segments.length - 1];
        return playerIdStr;
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
