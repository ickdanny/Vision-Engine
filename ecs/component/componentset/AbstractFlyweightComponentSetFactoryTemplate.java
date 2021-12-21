package ecs.component.componentset;

import util.observer.AbstractPushSubject;
import util.observer.ConfigurablePushSubject;

abstract class AbstractFlyweightComponentSetFactoryTemplate
        implements AbstractFlyweightComponentSetFactory {
    protected final ConfigurablePushSubject<AbstractComponentSet> newComponentSetBroadcaster;

    protected AbstractFlyweightComponentSetFactoryTemplate(){
        newComponentSetBroadcaster = new ConfigurablePushSubject<>();
    }

    @Override
    public AbstractPushSubject<AbstractComponentSet> getNewComponentSetBroadcaster() {
        return newComponentSetBroadcaster;
    }
}