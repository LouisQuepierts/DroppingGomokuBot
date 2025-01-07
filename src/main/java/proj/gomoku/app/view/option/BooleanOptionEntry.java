package proj.gomoku.app.view.option;

import javafx.scene.Node;
import net.quepierts.papyri.model.option.BooleanOption;
import net.quepierts.papyri.model.option.OptionEntry;
import proj.gomoku.app.view.ToggleButton;

public class BooleanOptionEntry extends AbstractOptionEntry<Boolean> {
    private ToggleButton button;

    public BooleanOptionEntry(BooleanOption option) {
        super(option);
    }

    @Override
    protected Node getInputWidget(OptionEntry<Boolean> option) {
        this.button = new ToggleButton(option::setValue, option.getValue());
        return this.button;
    }

    @Override
    protected void onOptionChanged(Boolean value) {
        if (value != this.button.isEnabled()) {
            this.button.toggle();
        }
    }
}
