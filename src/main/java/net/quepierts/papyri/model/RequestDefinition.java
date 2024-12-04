package net.quepierts.papyri.model;

import net.quepierts.papyri.PapyriBoost;

import java.lang.reflect.Type;
import java.util.Map;

public record RequestDefinition(
        Type packed,
        boolean nullable,
        PapyriBoost.AssembleConstructor constructor,
        Map<String, ParameterDefinition> mapped
) {
}
