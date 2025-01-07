package net.quepierts.papyri.event;

import java.lang.invoke.MethodHandle;

public class SubscribeEventListener extends EventListener {
    private final MethodHandle handle;

    public SubscribeEventListener(MethodHandle handle) {
        this.handle = handle;
    }

    @Override
    public void invoke(Event event) {
        if (this.handle != null) {
            try {
                this.handle.invokeWithArguments(event);
            } catch (Throwable ignored) {}
        }
    }
}
