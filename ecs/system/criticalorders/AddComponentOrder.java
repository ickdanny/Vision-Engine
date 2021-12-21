package ecs.system.criticalorders;

import ecs.component.AbstractComponentType;
import ecs.component.TypeComponentTuple;
import ecs.entity.EntityHandle;

public class AddComponentOrder<T> extends TypeComponentTuple<T> {
    private final EntityHandle entityHandle;

    public AddComponentOrder(EntityHandle entityHandle, AbstractComponentType<T> type, T component) {
        super(type, component);
        this.entityHandle = entityHandle;
    }

    public EntityHandle getEntityHandle(){
        return entityHandle;
    }
}