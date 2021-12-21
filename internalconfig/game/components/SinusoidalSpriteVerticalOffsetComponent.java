package internalconfig.game.components;

public class SinusoidalSpriteVerticalOffsetComponent {
    private final double tick;
    private final double amplitude;
    private double currentTick;

    public SinusoidalSpriteVerticalOffsetComponent(double tick, double amplitude) {
        this.currentTick = 0;
        this.tick = tick;
        this.amplitude = amplitude;
    }

    public double getTick() {
        return tick;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(double currentTick) {
        this.currentTick = currentTick;
    }
}