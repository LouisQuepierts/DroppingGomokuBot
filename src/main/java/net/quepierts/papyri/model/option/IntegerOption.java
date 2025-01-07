package net.quepierts.papyri.model.option;

import lombok.Setter;

@Setter
public class IntegerOption extends OptionEntry<Integer> {
    private int value;

    public IntegerOption(String name) {
        super(name);
    }

    public IntegerOption(String name, int value) {
        super(name);
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    protected void setValueInner(Integer newValue) {
        this.value = newValue;
    }

}
