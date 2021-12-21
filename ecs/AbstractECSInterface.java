package ecs;

import ecs.datastorage.AbstractDataStorage;
import util.messaging.AbstractPublishSubscribeBoard;

public interface AbstractECSInterface {
    AbstractPublishSubscribeBoard getGlobalBoard();
    AbstractPublishSubscribeBoard getSliceBoard();
    AbstractDataStorage getSliceData();
}
