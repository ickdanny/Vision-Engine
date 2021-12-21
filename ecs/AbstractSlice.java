package ecs;

import ecs.system.AbstractSystemChainCall;

interface AbstractSlice {
    boolean isTransparent(AbstractSystemChainCall<?> call);
    <T> void runSystemChain(AbstractSystemChainCall<T> call);
    String getName();
}