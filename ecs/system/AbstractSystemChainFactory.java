package ecs.system;

import ecs.AbstractECSInterface;

@SuppressWarnings("unused")
public interface AbstractSystemChainFactory<T> {
    AbstractSystemChain<T> makeSystemChain(AbstractECSInterface ecsInterface);
    int getNumSystems();
}