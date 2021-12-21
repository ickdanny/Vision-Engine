package ecs.component.componentstorage;

import ecs.component.componentset.AbstractComponentSet;
import ecs.component.AbstractComponentType;
import ecs.component.componentset.AbstractFlyweightComponentSetFactory;
import ecs.component.TypeComponentTuple;
import ecs.system.criticalorders.AddComponentOrder;
import ecs.system.criticalorders.AddEntityOrder;
import ecs.system.criticalorders.RemoveComponentOrder;
import ecs.system.criticalorders.RemoveEntityOrder;
import ecs.system.criticalorders.SetComponentOrder;

@SuppressWarnings("unused")
public class ComponentStorage implements AbstractComponentStorage {

    private final AbstractGroupManager groupManager;

    public ComponentStorage(AbstractFlyweightComponentSetFactory componentSetFactory,
                            AbstractComponentType<?>[] types, boolean isEntityCountStatic, int entityCount) {

        ArchetypeFactory archetypeFactory = new ArchetypeFactory(types, isEntityCountStatic, entityCount);
        componentSetFactory.getNewComponentSetBroadcaster().attach(archetypeFactory.getNewComponentSetReceiver());

        GroupManager groupManager = new GroupManager();
        archetypeFactory.getNewArchetypeBroadcaster().attach(groupManager.getNewArchetypeReceiver());
        groupManager.init(types, componentSetFactory);
        this.groupManager = groupManager;
    }

    @Override
    public AbstractGroup createGroup(AbstractComponentSet set) {
        return groupManager.createGroup(set);
    }

    @Override
    public <T> T getComponent(int entityID, AbstractComponentType<T> type, AbstractComponentSet set) {
        AbstractArchetype archetype = set.getAssociatedArchetype();
        return archetype.getComponent(entityID, type);
    }

    @Override
    public <T> void addComponent(AddComponentOrder<T> addComponentOrder,
                                 AbstractComponentSet oldSet, AbstractComponentSet newSet) {

        int entityID = addComponentOrder.getEntityHandle().getEntityID();
        AbstractComponentType<T> type = addComponentOrder.getType();
        T component = addComponentOrder.getComponent();

        if(oldSet == newSet){
            throw new RuntimeException("trying to add component entity " + entityID + " already has");
        }

        AbstractArchetype oldArchetype = oldSet.getAssociatedArchetype();
        AbstractArchetype newArchetype = newSet.getAssociatedArchetype();

        oldArchetype.moveEntity(entityID, newArchetype);
        if(component != null) {
            newArchetype.setComponent(entityID, type, component);
        }
    }

    @Override
    public <T> void setComponent(SetComponentOrder<T> setComponentOrder,
                                 AbstractComponentSet oldSet, AbstractComponentSet newSet){
        int entityID = setComponentOrder.getEntityHandle().getEntityID();
        AbstractComponentType<T> type = setComponentOrder.getType();
        T component = setComponentOrder.getComponent();

        AbstractArchetype newArchetype = newSet.getAssociatedArchetype();

        if(oldSet != newSet){
            AbstractArchetype oldArchetype = oldSet.getAssociatedArchetype();
            oldArchetype.moveEntity(entityID, newArchetype);
        }

        if(component != null){
            newArchetype.setComponent(entityID, type, component);
        }
    }

    @Override
    public void removeComponent(RemoveComponentOrder removeComponentOrder,
                                AbstractComponentSet oldSet, AbstractComponentSet newSet){
        int entityID = removeComponentOrder.getEntityHandle().getEntityID();
        AbstractComponentType<?> type = removeComponentOrder.getType();

        if(oldSet != newSet) {
            AbstractArchetype oldArchetype = oldSet.getAssociatedArchetype();
            AbstractArchetype newArchetype = newSet.getAssociatedArchetype();
            //moving cuts off hanging components
            oldArchetype.moveEntity(entityID, newArchetype);
        }
    }

    @Override
    public void addEntity(AddEntityOrder addEntityOrder, int entityID, AbstractComponentSet set){
        AbstractArchetype archetype = set.getAssociatedArchetype();
        for(TypeComponentTuple<?> tuple : addEntityOrder.getComponents()){
            if(!tuple.getType().isMarker()) {
                addComponentForEntity(entityID, tuple, archetype);
            }
        }
    }

    private <T> void addComponentForEntity(int entityID, TypeComponentTuple<T> tuple, AbstractArchetype archetype){
        T component = tuple.getComponent();
        if(component != null){
            archetype.setComponent(entityID, tuple.getType(), component);
        }
    }

    @Override
    public void removeEntity(RemoveEntityOrder removeEntityOrder, AbstractComponentSet set){
        int entityID = removeEntityOrder.getEntityHandle().getEntityID();
        set.getAssociatedArchetype().removeEntity(entityID);
    }
}
