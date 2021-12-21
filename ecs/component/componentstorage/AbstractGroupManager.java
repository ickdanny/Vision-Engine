package ecs.component.componentstorage;

import ecs.component.componentset.AbstractComponentSet;
import util.observer.AbstractPushObserver;

interface AbstractGroupManager {
    AbstractGroupTemplate createGroup(AbstractComponentSet set);
    AbstractPushObserver<AbstractArchetype> getNewArchetypeReceiver();
}