package ecs;

import ecs.entity.EntityHandle;
import ecs.entity.NamedEntityHandle;
import ecs.system.criticalorders.AddComponentOrder;
import ecs.system.criticalorders.AddEntityOrder;
import ecs.system.criticalorders.RemoveComponentOrder;
import ecs.system.criticalorders.RemoveEntityOrder;
import ecs.system.criticalorders.SetComponentOrder;
import util.messaging.Message;

import java.util.List;

import static ecs.ECSTopics.*;

public final class ECSUtil {
    public static Message<AddComponentOrder<?>> makeAddComponentMessage(AddComponentOrder<?> order){
        return new Message<>(ADD_COMPONENT_ORDERS, order, Message.AGELESS);
    }
    public static Message<SetComponentOrder<?>> makeSetComponentMessage(SetComponentOrder<?> order){
        return new Message<>(SET_COMPONENT_ORDERS, order, Message.AGELESS);
    }
    public static Message<RemoveComponentOrder> makeRemoveComponentMessage(RemoveComponentOrder order){
        return new Message<>(REMOVE_COMPONENT_ORDERS, order, Message.AGELESS);
    }
    public static Message<AddEntityOrder> makeAddEntityMessage(AddEntityOrder order){
        return new Message<>(ADD_ENTITY_ORDERS, order, Message.AGELESS);
    }
    public static Message<RemoveEntityOrder> makeRemoveEntityMessage(RemoveEntityOrder order){
        return new Message<>(REMOVE_ENTITY_ORDERS, order, Message.AGELESS);
    }

    public static EntityHandle getHandleForNamedEntity(String name, List<Message<NamedEntityHandle>> newNamedEntities){
        for(Message<NamedEntityHandle> message : newNamedEntities){
            if(message.getMessage().getName().equals(name)){
                return message.getMessage();
            }
        }
        throw new RuntimeException("could not find entity named: " + name);
    }
}