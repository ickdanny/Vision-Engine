package ecs;

import ecs.entity.EntityHandle;
import ecs.entity.NamedEntityHandle;
import ecs.system.criticalorders.AddComponentOrder;
import ecs.system.criticalorders.AddEntityOrder;
import ecs.system.criticalorders.RemoveComponentOrder;
import ecs.system.criticalorders.RemoveEntityOrder;
import ecs.system.criticalorders.SetComponentOrder;
import util.messaging.Topic;

public final class ECSTopics {
    public static final Topic<AddComponentOrder<?>> ADD_COMPONENT_ORDERS;
    public static final Topic<SetComponentOrder<?>> SET_COMPONENT_ORDERS;
    public static final Topic<RemoveComponentOrder> REMOVE_COMPONENT_ORDERS;
    public static final Topic<AddEntityOrder> ADD_ENTITY_ORDERS;
    public static final Topic<RemoveEntityOrder> REMOVE_ENTITY_ORDERS;

    public static final Topic<EntityHandle> REMOVED_ENTITIES;

    public static final Topic<EntityHandle> NEW_ANONYMOUS_ENTITIES;
    public static final Topic<NamedEntityHandle> NEW_NAMED_ENTITIES;


    public static final Topic<String> PUSH_NEW_SLICE;
    public static final Topic<String> POP_SLICE_BACK_TO;

    public static final Topic<?>[] ECS_TOPICS = {
            ADD_COMPONENT_ORDERS = new Topic<>(),
            SET_COMPONENT_ORDERS = new Topic<>(),
            REMOVE_COMPONENT_ORDERS = new Topic<>(),
            ADD_ENTITY_ORDERS = new Topic<>(),
            REMOVE_ENTITY_ORDERS = new Topic<>(),
            REMOVED_ENTITIES = new Topic<>(),
            NEW_ANONYMOUS_ENTITIES = new Topic<>(),
            NEW_NAMED_ENTITIES = new Topic<>(),
            PUSH_NEW_SLICE = new Topic<>(),
            POP_SLICE_BACK_TO = new Topic<>(),
    };
}