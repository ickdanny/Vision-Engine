package util.datastructure.lookuptable;

public class StaticArrayIntLookupTable<E> extends AbstractArrayIntLookupTable<E> {
    public StaticArrayIntLookupTable(int maxCapacity){
        super(maxCapacity, new UnsupportedGrowIndicesHandler(), new UnsupportedGrowValuesHandler());
    }
    public StaticArrayIntLookupTable(int maxIndex, int maxCapacity){
        super(maxIndex, maxCapacity, new UnsupportedGrowIndicesHandler(), new UnsupportedGrowValuesHandler());
    }
}