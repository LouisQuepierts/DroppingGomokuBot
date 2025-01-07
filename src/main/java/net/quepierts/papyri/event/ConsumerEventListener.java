package net.quepierts.papyri.event;

import java.util.function.Consumer;

public class ConsumerEventListener extends EventListener {
    private final Consumer<Event> consumer;

    public ConsumerEventListener(Consumer<Event> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void invoke(Event event) {
        this.consumer.accept(event);
    }
}
