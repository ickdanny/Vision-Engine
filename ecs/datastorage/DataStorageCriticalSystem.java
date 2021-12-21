package ecs.datastorage;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSingleInstanceSystem;

public class DataStorageCriticalSystem<T> extends AbstractSingleInstanceSystem<T> {
        @Override
        public void run(AbstractECSInterface ecsInterface, T data) {
            ((AbstractDataStorage_PP) ecsInterface.getSliceData()).carryOutCriticalOrders();
            ecsInterface.getSliceBoard().ageAndCullMessages();
        }
}