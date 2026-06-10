package util.datastructure.lookuptable;

import java.util.Arrays;

@SuppressWarnings("unused")
abstract class AbstractArrayIntLookupTable<E> extends AbstractArrayLookupTableTemplate
        implements AbstractIntLookupTable<E>{

    protected E[] values;

    protected AbstractArrayIntLookupTable(int capacity,
                                          AbstractGrowIndicesHandler growIndicesHandler,
                                          AbstractGrowValuesHandler growValuesHandler){
        super(capacity, growIndicesHandler, growValuesHandler);
        values = makeGenericArray(capacity);
    }

    protected AbstractArrayIntLookupTable(int indexCapacity, int valueCapacity,
                                          AbstractGrowIndicesHandler growIndicesHandler,
                                          AbstractGrowValuesHandler growValuesHandler){
        super(indexCapacity, valueCapacity, growIndicesHandler, growValuesHandler);
        values = makeGenericArray(valueCapacity);
    }

    @SuppressWarnings("unchecked")
    protected E[] makeGenericArray(int capacity){
        return (E[])new Object[capacity];
    }

    @Override
    protected void clearValues(){
        Arrays.fill(values, null);
    }

    @Override
    protected void clearValuesAfter(int indexInclusive) {
        Arrays.fill(values, indexInclusive, values.length, null);
    }

    @Override
    public void clear(){
        super.clear();
    }

    @Override
    public int size(){
        return super.size();
    }

    @Override
    public boolean contains(int i) {
        return super.contains(i);
    }

    @Override
    public E get(int i) {
        if(i >= indices.length){
            return null;
        }
        int index = indices[i];
        if(isInvalid(index)){
            return null;
        }
        return values[index];
    }

    @Override
    public boolean set(int i, E e) {
        growIndicesIfNecessary(i);
        int index = indices[i];
        if(isInvalid(index)) {
            appendToBack(i, e);
            return true;
        }
        else{ //change value if that index was previously in use
            values[index] = e;
            return false;
        }
    }

    protected void appendToBack(int i, E e){
        growValuesIfNecessary();
        indices[i] = currentSize;
        values[currentSize] = e;
        valueToIndex[currentSize] = i;
        ++currentSize;
    }

    @Override
    public boolean remove(int i) {
        return super.remove(i);
    }

    @Override
    protected int getValuesLength() {
        return values.length;
    }

    @Override
    protected void overwriteValueFromAToB(int indexA, int indexB) {
        values[indexB] = values[indexA];
    }

    @Override
    protected void valueSwap(int indexA, int indexB){
        //swap in values
        E tempValue = values[indexA];
        values[indexA] = values[indexB];
        values[indexB] = tempValue;

        //swap in indices
        int indicesPositionA = valueToIndex[indexA];
        int indicesPositionB = valueToIndex[indexB];
        int tempIndex = indices[indicesPositionA];
        indices[indicesPositionA] = indices[indicesPositionB];
        indices[indicesPositionB] = tempIndex;

        //swap in valueToIndex
        int tempValueToIndex = valueToIndex[indexA];
        valueToIndex[indexA] = valueToIndex[indexB];
        valueToIndex[indexB] = tempValueToIndex;
    }

    @Override
    protected void invalidateValue(int index) {
        values[index] = null;
    }

    @Override
    public LookupTableIterator<E> iterator(){
        return new Itr();
    }

    private class Itr implements LookupTableIterator<E>{

        private int cursor;
        private int indexOfPreviousValue;

        private Itr(){
            cursor = 0;
            indexOfPreviousValue = INVALID_INDEX;
        }

        @Override
        public boolean hasNext() {
            return cursor < currentSize;
        }
        @Override
        public E next() {
            indexOfPreviousValue = valueToIndex[cursor];
            return values[cursor++];
        }
        @Override
        public int indexOfPreviousValue() {
            return indexOfPreviousValue;
        }
    }

    protected static class GrowIndicesHandler implements AbstractGrowIndicesHandler{
        @Override
        public void growIndices(int i, AbstractArrayLookupTableTemplate lookupTable) {
            int oldLength = lookupTable.indices.length;
            lookupTable.indices = Arrays.copyOf(lookupTable.indices, newLength(i));
            lookupTable.clearIndicesAfter(oldLength);
        }
    }
    protected static class GrowValuesHandler implements AbstractGrowValuesHandler{
        @Override
        public void growValues(AbstractArrayLookupTableTemplate lookupTablePreCast) {
            growValuesGenericHelper((AbstractArrayIntLookupTable<?>)lookupTablePreCast);
        }

        private <T> void growValuesGenericHelper(AbstractArrayIntLookupTable<T> lookupTable){
            int oldLength = lookupTable.values.length;
            lookupTable.values = Arrays.copyOf(lookupTable.values, newLength(oldLength));
            lookupTable.valueToIndex = Arrays.copyOf(lookupTable.valueToIndex, lookupTable.values.length);
            lookupTable.clearValueToIndexAfter(oldLength);
        }
    }
}
