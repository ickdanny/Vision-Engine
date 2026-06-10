package util.flyweight;

import util.tuple.Tuple2;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Spliterator;

//the implementation of this class is modeled after HashSet
//does not permit null elements
@SuppressWarnings("unused")
public class FlyweightHashSet<E> extends AbstractSet<E> implements AbstractFlyweightSet<E>, Cloneable{ //not implementing serializable for now

    private HashMap<E, E> map;

    public FlyweightHashSet() {
        map = new HashMap<>();
    }
    public FlyweightHashSet(Collection<? extends E> c) {
        map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
        addAll(c);
    }
    public FlyweightHashSet(int initialCapacity, float loadFactor) {
        map = new HashMap<>(initialCapacity, loadFactor);
    }
    public FlyweightHashSet(int initialCapacity) {
        map = new HashMap<>(initialCapacity);
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean contains(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        return map.containsKey(o);
    }

    @Override
    public boolean add(E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        E prevElement = map.put(e, e);
        if(prevElement == null){
            //this set did not previously contain the element
            return true;
        }
        //put this element back;
        map.put(e, prevElement);
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        E value = map.remove(o);
        return value != null;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object clone() {
        try {
            FlyweightHashSet<E> newSet = (FlyweightHashSet<E>) super.clone();
            newSet.map = (HashMap<E, E>)map.clone();
            return newSet;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public Spliterator<E> spliterator() {
        //need to cast to a hashSet, for access to the spliterator (package private implementation)
        return toHashSet().spliterator();
    }
    private HashSet<E> toHashSet(){
        return new HashSet<>(map.keySet());
    }

    @Override
    public E get(E e) {
        if (e == null) {
            throw new NullPointerException();
        }

        E prevElement = map.get(e);
        if(prevElement == null){
            prevElement = e;
            add(prevElement);
        }
        return prevElement;
    }

    @Override
    public Tuple2<E, Boolean> getAndCheckIfNew(E e) {
        if (e == null) {
            throw new NullPointerException();
        }

        E prevElement = map.get(e);
        if(prevElement == null){
            prevElement = e;
            add(prevElement);
            return new Tuple2<>(prevElement, true);
        }
        return new Tuple2<>(prevElement, false);
    }
}