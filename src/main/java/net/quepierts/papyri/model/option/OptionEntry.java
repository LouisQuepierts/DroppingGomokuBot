package net.quepierts.papyri.model.option;

import lombok.Getter;
import lombok.Setter;
import net.quepierts.papyri.event.OptionUpdateEvent;
import net.quepierts.papyri.event.PapyriEventBus;

import java.util.function.Consumer;

public abstract class OptionEntry<T> {
    @Getter
    private final String name;

    @Setter
    private Consumer<T> callback;

    protected OptionEntry(String name) {
        this.name = name;
    }

    public void setValue(T newValue) {
        this.setValueInner(newValue);
        PapyriEventBus.post(new OptionUpdateEvent<>(this));

        if (this.callback != null) {
            this.callback.accept(newValue);
        }
    }

    public abstract T getValue();

    protected abstract void setValueInner(T newValue);
}
