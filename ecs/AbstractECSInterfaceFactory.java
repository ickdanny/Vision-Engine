package ecs;

import ecs.datastorage.AbstractDataStorage;
import util.messaging.AbstractPublishSubscribeBoard;

interface AbstractECSInterfaceFactory {
    AbstractECSInterface makeECSInterface(AbstractPublishSubscribeBoard globalBoard,
                                          AbstractPublishSubscribeBoard sliceBoard,
                                          AbstractDataStorage dataStorage);
}
