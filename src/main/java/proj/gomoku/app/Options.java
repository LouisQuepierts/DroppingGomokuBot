package proj.gomoku.app;

import net.quepierts.papyri.model.option.BooleanOption;
import net.quepierts.papyri.model.option.IntegerOption;

public class Options {
    public static final BooleanOption ENABLED_AI = new BooleanOption("Enable AI", false);

    public static final BooleanOption BLUE_RED = new BooleanOption("Blue / Red", false);

    public static final BooleanOption ENABLED_DEBUG = new BooleanOption("Enable Debug", false);

    public static final BooleanOption STEP_DEBUG = new BooleanOption("Stepped Debug", false);

    public static final IntegerOption STEP_DELAY = new IntegerOption("Step Delay", 50);
}
