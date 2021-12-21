package ecs.component;

import ecs.entity.EntityHandle;
import util.messaging.Topic;

public interface AbstractComponentType<T> {
    int getIndex();
    default boolean isMarker(){
        return false;
    }
    Topic<EntityHandle> getSetComponentTopic();
    Topic<EntityHandle> getRemoveComponentTopic();
}