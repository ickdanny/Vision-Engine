package util.datastructure.lookuptable;

import util.primitiveiterator.IntIterator;

public interface IntLookupTableIterator extends IntIterator {
    int indexOfPreviousValue();
}