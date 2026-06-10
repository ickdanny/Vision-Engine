package util.datastructure.lookuptable;

public class DynamicValuesArrayIntLookupTable<E> extends AbstractArrayIntLookupTable<E> {
    public DynamicValuesArrayIntLookupTable(int maxIndex, int initCapacity){
        super(maxIndex, Math.min(initCapacity, maxIndex), new UnsupportedGrowIndicesHandler(), new GrowValuesHandler());
    }
}
