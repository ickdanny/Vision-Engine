package util.datastructure.lookuptable;

public interface AbstractIntLookupTable<E> {
    void clear();
    int size();
    boolean contains(int i);
    E get(int i);
    boolean set(int i, E e); //return true if element was not already occupied - false if element was replaced
    boolean remove(int i); //return true if element was extant.

    LookupTableIterator<E> iterator();
}