package ecs.component.componentstorage;

import ecs.component.componentset.AbstractComponentSet;
import ecs.component.AbstractComponentType;

@SuppressWarnings("UnusedReturnValue")
public abstract class AbstractArchetype {

    public abstract <T> T getComponent(int entityID, AbstractComponentType<T> type);
    public abstract <T> boolean setComponent(int entityID, AbstractComponentType<T> type, T component);
    protected abstract <T> boolean setComponent(int entityID, int typeIndex, T component);
    public abstract void moveEntity(int entityID, AbstractArchetype newArchetype);
    public abstract boolean removeEntity(int entityID);

    public abstract AbstractComponentSet getComponentKey();
    public abstract <T> ComponentIterator<T> getComponentIterator(AbstractComponentType<T> type);
}