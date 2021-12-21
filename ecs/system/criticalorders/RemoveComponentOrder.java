package ecs.system.criticalorders;

import ecs.component.AbstractComponentType;
import ecs.entity.EntityHandle;

public class RemoveComponentOrder{
    private final EntityHandle entityHandle;
    private final AbstractComponentType<?> type;

    public RemoveComponentOrder(EntityHandle entityHandle, AbstractComponentType<?> type) {
        this.entityHandle = entityHandle;
        this.type = type;
    }

    public EntityHandle getEntityHandle(){
        return entityHandle;
    }

    public AbstractComponentType<?> getType() {
        return type;
    }
}