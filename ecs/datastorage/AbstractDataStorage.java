package ecs.datastorage;

import ecs.component.AbstractComponentType;
import ecs.component.componentstorage.AbstractGroup;
import ecs.entity.EntityHandle;

@SuppressWarnings("unused")
public interface AbstractDataStorage {

    AbstractGroup createGroup(AbstractComponentType<?>... types);

    boolean isAlive(EntityHandle entityHandle);
    boolean isDead(EntityHandle entityHandle);

    boolean containsComponent(EntityHandle entityHandle, AbstractComponentType<?> type);
    boolean containsAllComponents(EntityHandle entityHandle, AbstractComponentType<?>... types);
    boolean containsAnyComponent(EntityHandle entityHandle, AbstractComponentType<?>... types);

    <T> T getComponent(EntityHandle entityHandle, AbstractComponentType<T> type);

    EntityHandle makeHandle(int entityID);

    int getMessageLifetime();
}