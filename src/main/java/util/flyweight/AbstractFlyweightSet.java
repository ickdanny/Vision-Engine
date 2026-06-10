package util.flyweight;

import util.tuple.Tuple2;

import java.util.Set;

public interface AbstractFlyweightSet<E> extends Set<E> {
    E get(E e);
    Tuple2<E, Boolean> getAndCheckIfNew(E e); //true if we added a new element
}