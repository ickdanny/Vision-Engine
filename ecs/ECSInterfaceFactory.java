package ecs;

import ecs.datastorage.AbstractDataStorage;
import util.messaging.AbstractPublishSubscribeBoard;

class ECSInterfaceFactory implements AbstractECSInterfaceFactory {

    private final AbstractSliceStack sliceStack;

    ECSInterfaceFactory(AbstractSliceStack sliceStack) {
        this.sliceStack = sliceStack;
    }

    @Override
    public AbstractECSInterface makeECSInterface(AbstractPublishSubscribeBoard globalBoard,
                                                 AbstractPublishSubscribeBoard sliceBoard,
                                                 AbstractDataStorage dataStorage) {
        return new ECSInterface(globalBoard, sliceBoard, dataStorage, sliceStack);
    }
}
