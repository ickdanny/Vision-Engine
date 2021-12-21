package ecs.component.componentstorage;

import ecs.component.componentset.AbstractComponentSet;
import ecs.component.AbstractComponentType;
import util.observer.AbstractPushObserver;
import util.observer.AbstractPushSubject;
import util.observer.ConfigurablePushSubject;

class ArchetypeFactory {

    private final AbstractComponentType<?>[] types;
    private final boolean isEntityCountStatic;
    private final int entityCount;

    private final ConfigurablePushSubject<AbstractArchetype> newArchetypeBroadcaster;
    private final AbstractPushObserver<AbstractComponentSet> newComponentSetReceiver;

    public ArchetypeFactory(AbstractComponentType<?>[] types, boolean isEntityCountStatic, int entityCount) {
        this.types = types;
        this.isEntityCountStatic = isEntityCountStatic;
        this.entityCount = entityCount;
        newArchetypeBroadcaster = new ConfigurablePushSubject<>();
        newComponentSetReceiver = makeNewComponentSetTypeReceiver();
    }

    private AbstractPushObserver<AbstractComponentSet> makeNewComponentSetTypeReceiver(){
        return this::makeArchetype;
    }

    private void makeArchetype(AbstractComponentSet data){
        Archetype newArchetype = new Archetype(types, data, isEntityCountStatic, entityCount);
        data.associateArchetype(newArchetype);
        newArchetypeBroadcaster.setPushData(newArchetype);
        newArchetypeBroadcaster.broadcast();
    }

    public AbstractPushSubject<AbstractArchetype> getNewArchetypeBroadcaster(){
        return newArchetypeBroadcaster;
    }

    public AbstractPushObserver<AbstractComponentSet> getNewComponentSetReceiver() {
        return newComponentSetReceiver;
    }
}
