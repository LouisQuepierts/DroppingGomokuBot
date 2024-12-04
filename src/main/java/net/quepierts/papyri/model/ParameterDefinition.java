package net.quepierts.papyri.model;

import java.lang.reflect.Type;

public record ParameterDefinition(
        Type type,
        boolean nullable,
        int ordinal
) {
}
