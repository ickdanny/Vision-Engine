package util.datastructure.lookuptable;

import util.primitiveiterator.IntIterable;

@SuppressWarnings("unused")
public interface AbstractIntIntLookupTable extends IntIterable {

    int INVALID_VALUE = Integer.MIN_VALUE;

    void clear();
    int size();
    boolean contains(int i);
    int get(int i);
    boolean set(int i, int e); //return true if element was not already occupied - false if element was replaced
    boolean remove(int i); //return true if element was extant.

    @Override
    IntLookupTableIterator intIterator();
}
