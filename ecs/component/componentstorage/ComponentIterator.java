package ecs.component.componentstorage;

import java.util.Iterator;

public interface ComponentIterator<T> extends Iterator<T> {
    int entityIDOfPreviousComponent();
}