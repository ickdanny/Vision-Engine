package ecs.component.componentset;

import ecs.component.AbstractComponentType;
import util.observer.AbstractPushSubject;

public interface AbstractFlyweightComponentSetFactory {
    AbstractComponentSet makeSet();
    AbstractComponentSet makeSet(AbstractComponentType<?>...types);
    AbstractComponentSet addComponent(AbstractComponentSet base, AbstractComponentType<?> type);
    AbstractComponentSet addComponents(AbstractComponentSet base, AbstractComponentType<?>... types);
    AbstractComponentSet removeComponent(AbstractComponentSet base, AbstractComponentType<?> type);
    AbstractComponentSet removeComponents(AbstractComponentSet base, AbstractComponentType<?>... types);

    AbstractPushSubject<AbstractComponentSet> getNewComponentSetBroadcaster();
}