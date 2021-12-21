package ecs;

import util.messaging.PublishSubscribeBoard;

public interface AbstractSliceProvider {
    void init(PublishSubscribeBoard globalBoard, AbstractECSInterfaceFactory ecsInterfaceFactory);
    AbstractSlice getSlice();
    String getName();
}