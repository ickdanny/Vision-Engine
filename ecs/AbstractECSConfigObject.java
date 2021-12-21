package ecs;

import util.messaging.Topic;

public interface AbstractECSConfigObject {
    AbstractSliceProvider[] getSliceProviders();
    String getBaseSlice();
    Topic<?>[] getGlobalTopics();
}