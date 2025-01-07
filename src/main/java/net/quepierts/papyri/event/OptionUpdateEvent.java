package net.quepierts.papyri.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.quepierts.papyri.model.option.OptionEntry;

@Getter
@AllArgsConstructor
public class OptionUpdateEvent<T extends OptionEntry<?>> extends Event {
    private final T option;
}
