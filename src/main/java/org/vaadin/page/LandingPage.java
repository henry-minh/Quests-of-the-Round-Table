package org.vaadin.page;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.Initializer;
import org.vaadin.Player;

@Route("")
@PageTitle("Landing Page")
@CssImport("./styles/LandingPageTheme.css")
public class LandingPage extends VerticalLayout {
    @Autowired
    private Initializer playerCreator;

    public LandingPage(){
        UI.getCurrent().getElement().setAttribute("class","startPage");
        this.setAlignItems(Alignment.CENTER);
        Div container = new Div();
        container.addClassName("container");

        Div playBtn = new Div();
        playBtn.addClassName("btn");
        playBtn.add(new Span("Play"));

        Div dot = new Div();
        dot.addClassName("dot");

        Div text = new Div();
        TextField name = new TextField();
        name.setClassName("NameText");
        name.setPlaceholder("Name...");
        text.add(name);

        container.add(playBtn);
        playBtn.add(dot);

        add(container,text);

        container.addClickListener(click -> Redirect(name.getValue()));
    }

    private void Redirect(String name){

        if(!name.isEmpty()){
            if(playerCreator.hasGameStarted() == true){
                createPrompt("Game has already started");
                return;
            }
            Player player = playerCreator.joinGame(name);
            if(player != null){
                UI.getCurrent().navigate("Player/" + player.getPlayerID());
            }
        }
        else{
            createPrompt("Please enter a name");
        }
    }
    private void createPrompt(String text){
        Span notiText = new Span(text);
        notiText.setClassName("notiText");
        Notification prompt = new Notification();
        prompt.add(notiText);
        prompt.setPosition(Notification.Position.BOTTOM_CENTER);
        prompt.setDuration(3000);
        prompt.open();
        prompt.show("");
        prompt.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        add(prompt);
    }
}
