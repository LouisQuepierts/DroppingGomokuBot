package proj.gomoku.app.view;

import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration;

public class DoublePropertyTransition extends Transition {
    private static final Duration DEFAULT_DURATION = Duration.millis(400.0);

    private ObjectProperty<Duration> duration;

    private DoubleProperty property;
    private DoubleProperty from;
    private DoubleProperty to;

    private double start;
    private double delta;

    public DoublePropertyTransition(Duration duration, DoubleProperty property) {
        this.setDuration(duration);
        this.property = property;
    }

    public final void setDuration(Duration var1) {
        if (this.duration != null || !DEFAULT_DURATION.equals(var1)) {
            this.durationProperty().set(var1);
        }
    }

    public final Duration getDuration() {
        return this.duration == null ? DEFAULT_DURATION : (Duration)this.duration.get();
    }

    public final ObjectProperty<Duration> durationProperty() {
        if (this.duration == null) {
            this.duration = new ObjectPropertyBase<>(DEFAULT_DURATION) {
                public void invalidated() {
                    try {
                        DoublePropertyTransition.this.setCycleDuration(DoublePropertyTransition.this.getDuration());
                    } catch (IllegalArgumentException var2) {
                        if (this.isBound()) {
                            this.unbind();
                        }

                        this.set(DoublePropertyTransition.this.getCycleDuration());
                        throw var2;
                    }
                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "duration";
                }
            };
        }

        return this.duration;
    }

    public final void setFrom(double var1) {
        if (this.from != null || !Double.isNaN(var1)) {
            this.fromProperty().set(var1);
        }
    }

    public final double getFromValue() {
        return this.from == null ? Double.NaN : this.from.get();
    }

    public final DoubleProperty fromProperty() {
        if (this.from == null) {
            this.from = new SimpleDoubleProperty(this, "from", Double.NaN);
        }

        return this.from;
    }

    public final void setTo(double var1) {
        if (this.to != null || !Double.isNaN(var1)) {
            this.toProperty().set(var1);
        }

    }

    public final double getToValue() {
        return this.to == null ? Double.NaN : this.to.get();
    }

    public final DoubleProperty toProperty() {
        if (this.to == null) {
            this.to = new SimpleDoubleProperty(this, "to", Double.NaN);
        }

        return this.to;
    }

    public void setProperty(double property) {
        this.property.set(property);
    }

    public double getProperty() {
        return property.get();
    }

    @Override
    public void play() {
        double fromValue = this.getFromValue();
        double toValue = this.getToValue();
        this.start = !Double.isNaN(fromValue) ? fromValue : this.property.getValue();
        this.delta = !Double.isNaN(toValue) ? toValue - this.start : 0.0;
        super.play();
    }

    @Override
    protected void interpolate(double v) {
        double var3 = this.start + v * this.delta;
        this.property.setValue(var3);
    }
}
