package util.datastructure.lookuptable;

import java.util.Arrays;

@SuppressWarnings("unused")
abstract class AbstractArrayIntIntLookupTable extends AbstractArrayLookupTableTemplate
        implements AbstractIntIntLookupTable{

    @SuppressWarnings("CanBeFinal")
    protected int[] values;

    public AbstractArrayIntIntLookupTable(int maxCapacity,
                                          AbstractGrowIndicesHandler growIndicesHandler,
                                          AbstractGrowValuesHandler growValuesHandler){
        super(maxCapacity, growIndicesHandler, growValuesHandler);
        values = new int[maxCapacity];
        clearValues();
    }

    public AbstractArrayIntIntLookupTable(int maxIndex, int maxCapacity,
                                          AbstractGrowIndicesHandler growIndicesHandler,
                                          AbstractGrowValuesHandler growValuesHandler){
        super(maxIndex, maxCapacity, growIndicesHandler, growValuesHandler);
        values = new int[maxCapacity];
        clearValues();
    }

    @Override
    protected void clearValues(){
        Arrays.fill(values, INVALID_VALUE);
    }

    @Override
    protected void clearValuesAfter(int indexInclusive){
        Arrays.fill(values, indexInclusive, values.length, INVALID_VALUE);
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
    public int get(int i) {
        if(i >= indices.length){
            return INVALID_VALUE;
        }
        int index = indices[i];
        if(isInvalid(index)){
            return INVALID_VALUE;
        }
        return values[index];
    }

    @Override
    public boolean set(int i, int e) {
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

    protected void appendToBack(int i, int e){
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
        int tempValue = values[indexA];
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
        values[index] = INVALID_VALUE;
    }

    @Override
    public IntLookupTableIterator intIterator(){
        return new Itr();
    }

    @SuppressWarnings("unused")
    private class Itr implements IntLookupTableIterator{

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
        public int next() {
            indexOfPreviousValue = valueToIndex[cursor];
            return values[cursor++];
        }
        @Override
        public int indexOfPreviousValue() {
            return indexOfPreviousValue;
        }
    }
}
