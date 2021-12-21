package internalconfig.game.components;

import ecs.component.AbstractComponentType;

public interface AbstractComponentTypeContainer {
    AbstractComponentType<?>[] getArray();
    <U, T extends AbstractComponentType<U>> AbstractComponentType<U> getTypeInstance(Class<T > typeClass);
}