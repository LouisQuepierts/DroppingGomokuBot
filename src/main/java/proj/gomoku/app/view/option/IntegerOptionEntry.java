package proj.gomoku.app.view.option;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import net.quepierts.papyri.model.option.OptionEntry;

import java.util.regex.Pattern;

public class IntegerOptionEntry extends AbstractOptionEntry<Integer> {
    private static final Pattern PATTERN = Pattern.compile("^[1-9]\\d*");
    public IntegerOptionEntry(OptionEntry<Integer> option) {
        super(option);
    }

    @Override
    protected Node getInputWidget(OptionEntry<Integer> option) {
        TextField field = new TextField(Integer.toString(option.getValue()));
        field.setPrefColumnCount(4);
        field.setTextFormatter(new TextFormatter<>(change -> {
            String text = change.getControlNewText();
            if (PATTERN.matcher(text).matches()) {
                option.setValue(Integer.valueOf(text));
                return change;
            }
            return null;
        }));
        return field;
    }
}
