package ecs;

import ecs.system.AbstractSystemChainCall;

public class ECS {
    private final AbstractSliceStack sliceStack;

    public ECS(AbstractECSConfigObject config) {
        sliceStack = new SliceStack(config.getSliceProviders(), config.getBaseSlice(), config.getGlobalTopics());
    }

    public void receiveSystemChainCall(AbstractSystemChainCall<?> call){
        sliceStack.receiveSystemChainCall(call);
    }
}