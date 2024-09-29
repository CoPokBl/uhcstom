package net.mangolise.uhc;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class GameState {
    protected final List<EventListener<?>> listeners = new ArrayList<>();

    public abstract void start(Uhc uhc);

    public void stop() {
        stopListening();
    }

    protected <T extends Event> EventListener<T> listen(Class<T> event, Consumer<T> consumer) {
        EventListener<T> listener = EventListener.of(event, consumer);
        MinecraftServer.getGlobalEventHandler().addListener(listener);
        listeners.add(listener);
        return listener;
    }

    protected void stopListening() {
        listeners.forEach(listener -> MinecraftServer.getGlobalEventHandler().removeListener(listener));
    }
}
