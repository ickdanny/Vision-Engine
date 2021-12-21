package ecs;

import ecs.system.AbstractSystemChainCall;

interface AbstractSliceStack {
    void pushSlice(String name);
    void popSliceBackTo(String name);
    void receiveSystemChainCall(AbstractSystemChainCall<?> systemChainCall);
}