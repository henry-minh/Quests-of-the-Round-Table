package org.vaadin;

import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Broadcaster {
    @Autowired
    Mediator mediator;
    static Executor executor = Executors.newSingleThreadExecutor();

    static LinkedList<Consumer<String>> listeners = new LinkedList<>();

    public static synchronized Registration register(Consumer<String> listener) {
        listeners.add(listener);

        return () -> {
            synchronized (Broadcaster.class) {
                listeners.remove(listener);
            }
        };
    }

    public static synchronized void broadcast(String message) {
        System.out.println("broadcast: " + message);
        //
        // update main player
        //update other plater
        for (int i = 0; i < listeners.size(); i++) {
            int x = i;

            executor.execute(() -> listeners.get(x).accept(message));
        }
    }


}

