package internalconfig.game;

import ecs.system.AbstractSystemChainCall;

public enum SystemChainCalls implements AbstractSystemChainCall<Double> {
    MAIN(0, true),
    GRAPHICS(1, false)
    ;

    private final int index;
    private final boolean topDown;
    private double deltaTime;

    SystemChainCalls(int index, boolean topDown) {
        this.index = index;
        this.topDown = topDown;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean isTopDown() {
        return topDown;
    }

    @Override
    public Double getData() {
        return deltaTime;
    }

    public void setDeltaTime(double deltaTime) {
        this.deltaTime = deltaTime;
    }
}
