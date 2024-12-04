package proj.gomoku;

import com.google.inject.Binder;
import com.google.inject.Module;
import javafx.application.Application;
import net.quepierts.papyri.PapyriBoost;
import proj.gomoku.app.GomokuApplication;

import java.util.function.Consumer;

public class Main implements Consumer<String[]> {
    public static void main(String[] args) {
        PapyriBoost.start(Main.class, new ApplicationModule(), args);
    }

    @Override
    public void accept(String[] args) {
        Application.launch(GomokuApplication.class, args);
    }

    public static final class ApplicationModule implements Module {
        @Override
        public void configure(Binder binder) {

        }
    }
}
