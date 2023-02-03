package org.vaadin;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

@Push
@Route("broadcaster/:id([0-3]*)")
public class BroadcasterView extends Div {
    VerticalLayout messages = new VerticalLayout();
    Registration broadcasterRegistration;

    // Creating the UI shown separately

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = Broadcaster.register(newMessage -> {
            ui.access(() -> test());
        });
    }
    private void test(){

        messages.add(new Span("test function added"));
    }
    @Override
    protected void onDetach(DetachEvent detachEvent) {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }
    public BroadcasterView() {
        TextField message = new TextField();
        Button send = new Button("Send", e -> {
            Broadcaster.broadcast("default value");
            message.setValue("");
        });

        HorizontalLayout sendBar = new HorizontalLayout(message, send);

        add(sendBar, messages);
    }

    public void printFunction (){
        System.out.println("printFunction()");
        add(new H2("printFunctionRun"));
    }
}