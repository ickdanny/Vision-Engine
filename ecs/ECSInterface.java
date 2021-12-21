package ecs;

import ecs.datastorage.AbstractDataStorage;
import util.messaging.AbstractPublishSubscribeBoard;

class ECSInterface implements AbstractECSInterface_PP {

    private final AbstractPublishSubscribeBoard globalBoard;
    private final AbstractPublishSubscribeBoard sliceBoard;
    private final AbstractDataStorage sliceData;
    private final AbstractSliceStack sliceStack;

    ECSInterface(AbstractPublishSubscribeBoard globalBoard,
                        AbstractPublishSubscribeBoard sliceBoard,
                        AbstractDataStorage sliceData,
                        AbstractSliceStack sliceStack) {

        this.globalBoard = globalBoard;
        this.sliceBoard = sliceBoard;
        this.sliceData = sliceData;
        this.sliceStack = sliceStack;
    }

    @Override
    public AbstractPublishSubscribeBoard getGlobalBoard() {
        return globalBoard;
    }

    @Override
    public AbstractPublishSubscribeBoard getSliceBoard() {
        return sliceBoard;
    }

    @Override
    public AbstractDataStorage getSliceData() {
        return sliceData;
    }

    @Override
    public AbstractSliceStack getSliceStack() {
        return sliceStack;
    }
}
