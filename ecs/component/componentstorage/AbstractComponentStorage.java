package ecs.component.componentstorage;

import ecs.component.AbstractComponentType;
import ecs.component.componentset.AbstractComponentSet;
import ecs.system.criticalorders.AddComponentOrder;
import ecs.system.criticalorders.AddEntityOrder;
import ecs.system.criticalorders.RemoveComponentOrder;
import ecs.system.criticalorders.RemoveEntityOrder;
import ecs.system.criticalorders.SetComponentOrder;

public interface AbstractComponentStorage {
    AbstractGroup createGroup(AbstractComponentSet set);

    <T> T getComponent(int entityID, AbstractComponentType<T> type, AbstractComponentSet set);

    <T> void addComponent(AddComponentOrder<T> addComponentOrder,
                          AbstractComponentSet oldSet, AbstractComponentSet newSet);

    <T> void setComponent(SetComponentOrder<T> setComponentOrder,
                          AbstractComponentSet oldSet, AbstractComponentSet newSet);

    void removeComponent(RemoveComponentOrder removeComponentOrder,
                         AbstractComponentSet oldSet, AbstractComponentSet newSet);

    void addEntity(AddEntityOrder addEntityOrder, int entityID, AbstractComponentSet set);

    void removeEntity(RemoveEntityOrder removeEntityOrder, AbstractComponentSet set);
}