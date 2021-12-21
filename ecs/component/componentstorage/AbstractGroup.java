package ecs.component.componentstorage;

import ecs.component.AbstractComponentType;

@SuppressWarnings("unused")
public interface AbstractGroup {
    <T> ComponentIterator<T> getComponentIterator(AbstractComponentType<T> type);
    <T> ComponentIterator<T> getComponentIteratorExcludingTypes(AbstractComponentType<T> type,
                                                                       AbstractComponentType<?>... excludedTypes);
}