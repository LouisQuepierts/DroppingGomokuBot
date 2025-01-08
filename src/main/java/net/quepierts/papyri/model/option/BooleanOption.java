package net.quepierts.papyri.model.option;

public class BooleanOption extends OptionEntry<Boolean> {
    private boolean value;

    public BooleanOption(String name) {
        super(name);
    }

    public BooleanOption(String name, boolean value) {
        super(name);
        this.value = value;
    }

    @Override
    public Boolean getValue() {
        return this.value;
    }

    @Override
    protected void setValueInner(Boolean newValue) {
        this.value = newValue;
    }

    public void toggleValue() {
        this.value = !this.value;
    }

    public boolean getBooleanValue() {
        return this.value;
    }
}
