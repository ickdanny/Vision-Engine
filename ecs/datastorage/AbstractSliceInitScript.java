package ecs.datastorage;

import ecs.AbstractECSInterface;

public abstract class AbstractSliceInitScript {
    protected final void carryOutCriticalOrders(AbstractDataStorage dataStorage){
        ((AbstractDataStorage_PP)dataStorage).carryOutCriticalOrders();
    }
    public abstract void runOn(AbstractECSInterface ecsInterface);
}