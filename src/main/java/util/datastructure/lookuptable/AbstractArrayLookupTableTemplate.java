package util.datastructure.lookuptable;

import java.util.Arrays;

@SuppressWarnings("unused")
abstract class AbstractArrayLookupTableTemplate {

    protected static final double GROW_RATIO = 1.5;
    public static final int INVALID_INDEX = -1;

    protected int[] indices;
    protected int[] valueToIndex;
    protected int currentSize;

    protected final AbstractGrowIndicesHandler growIndicesHandler;
    protected final AbstractGrowValuesHandler growValuesHandler;

    protected AbstractArrayLookupTableTemplate(int capacity,
                                               AbstractGrowIndicesHandler growIndicesHandler,
                                               AbstractGrowValuesHandler growValuesHandler){
        indices = new int[capacity];
        clearIndices();
        valueToIndex = new int[capacity];
        clearValueToIndex();
        currentSize = 0;

        this.growIndicesHandler = growIndicesHandler;
        this.growValuesHandler = growValuesHandler;
    }

    protected AbstractArrayLookupTableTemplate(int indexCapacity, int valueCapacity,
                                               AbstractGrowIndicesHandler growIndicesHandler,
                                               AbstractGrowValuesHandler growValuesHandler){
        indices = new int[indexCapacity];
        clearIndices();
        valueToIndex = new int[valueCapacity];
        clearValueToIndex();
        currentSize = 0;

        this.growIndicesHandler = growIndicesHandler;
        this.growValuesHandler = growValuesHandler;
    }

    protected void clearIndices(){
        Arrays.fill(indices, INVALID_INDEX);
    }
    protected abstract void clearValues();
    protected void clearValueToIndex(){
        Arrays.fill(valueToIndex, INVALID_INDEX);
    }
    protected void clearIndicesAfter(int indexInclusive){
        Arrays.fill(indices, indexInclusive, indices.length, INVALID_INDEX);
    }
    protected abstract void clearValuesAfter(int indexInclusive);
    protected void clearValueToIndexAfter(int indexInclusive){
        Arrays.fill(valueToIndex, indexInclusive, valueToIndex.length, INVALID_INDEX);
    }

    protected void clear() {
        clearIndices();
        clearValues();
        clearValueToIndex();
        currentSize = 0;
    }

    protected int size() {
        return currentSize;
    }

    protected boolean contains(int i){
        return !isInvalid(indices[i]);
    }

    protected boolean remove(int i) {
        int index = indices[i];
        if(isInvalid(index)){ //meaning there was no such element to begin with
            return false;
        }

        --currentSize;
        if(currentSize > 0 && index != currentSize) { //swap with the last element
            overwriteWithElementAtCurrentSize(index);
        }
        else{
            removeElementAtIndex(currentSize);
        }

        return true;
    }

    protected void removeElementAtIndex(int index){
        invalidateIndex(valueToIndex[index]);
        invalidateValue(index);
        invalidateValueToIndex(index);
    }

    protected void overwriteWithElementAtCurrentSize(int index){
        overwriteFromAToB(currentSize, index);
    }

    protected void overwriteFromAToB(int indexA, int indexB) {
        overwriteValueFromAToB(indexA, indexB);
        invalidateValue(indexA);
        int indicesPositionOfA = valueToIndex[indexA];
        int indicesPositionOfB = valueToIndex[indexB];

        if (!isInvalid(indicesPositionOfA)) {
            indices[indicesPositionOfA] = indexB;
        }
        if (!isInvalid(indicesPositionOfB)) {
            invalidateIndex(indicesPositionOfB);
        }

        valueToIndex[indexB] = indicesPositionOfA;
        invalidateValueToIndex(indexA);
    }

    protected abstract void overwriteValueFromAToB(int indexA, int indexB);

    protected abstract void valueSwap(int indexA, int indexB);

    protected void growIndicesIfNecessary(int i){
        if(i >= indices.length){
            growIndicesHandler.growIndices(i, this);
        }
    }
    protected void growValuesIfNecessary(){
        if(currentSize >= getValuesLength()){
            growValuesHandler.growValues(this);
        }
    }

    protected static int newLength(int oldLength){
        return (int)(((double)oldLength) * GROW_RATIO) + 1;
    }
    protected abstract int getValuesLength();

    protected boolean isInvalid(int i){
        return i == INVALID_INDEX;
    }
    protected void invalidateIndex(int i){
        indices[i] = INVALID_INDEX;
    }
    protected abstract void invalidateValue(int index);
    protected void invalidateValueToIndex(int index){
        valueToIndex[index] = INVALID_INDEX;
    }

    protected interface AbstractGrowIndicesHandler{
        default void growIndices(int i, AbstractArrayLookupTableTemplate lookupTable){
            throw new UnsupportedOperationException("Cannot grow indices");
        }
    }
    protected interface AbstractGrowValuesHandler{
        default void growValues(AbstractArrayLookupTableTemplate lookupTable){
            throw new UnsupportedOperationException("Cannot grow values");
        }
    }
    protected static class UnsupportedGrowIndicesHandler implements AbstractGrowIndicesHandler{}
    protected static class UnsupportedGrowValuesHandler implements AbstractGrowValuesHandler{}
}
