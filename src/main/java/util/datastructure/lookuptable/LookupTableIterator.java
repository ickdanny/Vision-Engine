package util.datastructure.lookuptable;

import java.util.Iterator;

public interface LookupTableIterator<E> extends Iterator<E> {
    int indexOfPreviousValue();
}
