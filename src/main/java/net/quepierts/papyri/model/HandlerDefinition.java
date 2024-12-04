package net.quepierts.papyri.model;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;

public record HandlerDefinition(
        MethodHandle handle,
        RequestDefinition request,
        Type response
) {
}
