package ecs.system.criticalorders;

import ecs.entity.EntityHandle;

public class RemoveEntityOrder {
    private final EntityHandle entityHandle;

    public RemoveEntityOrder(EntityHandle entityHandle) {
        this.entityHandle = entityHandle;
    }

    public EntityHandle getEntityHandle(){
        return entityHandle;
    }
}
//I could get rid of this and just use message<Integer> but might need to add to this in the future