package ecs.system.criticalorders;

import ecs.component.AbstractComponentType;
import ecs.entity.EntityHandle;

public class SetComponentOrder<T> {
    private final EntityHandle entityHandle;
    private final AbstractComponentType<T> type;
    private final T component;

    public SetComponentOrder(EntityHandle entityHandle, AbstractComponentType<T> type, T component) {
        this.entityHandle = entityHandle;
        this.type = type;
        this.component = component;
    }

    public EntityHandle getEntityHandle(){
        return entityHandle;
    }

    public AbstractComponentType<T> getType() {
        return type;
    }

    public T getComponent() {
        return component;
    }
}