package util.datastructure.lookuptable;

public class DynamicIndicesArrayIntLookupTable<E> extends AbstractArrayIntLookupTable<E> {
    public DynamicIndicesArrayIntLookupTable(int initCapacity){
        super(initCapacity, new GrowIndicesHandler(), new UnsupportedGrowValuesHandler());
    }
    public DynamicIndicesArrayIntLookupTable(int initIndexCapacity, int maxValueCapacity){
        super(initIndexCapacity, maxValueCapacity, new GrowIndicesHandler(), new UnsupportedGrowValuesHandler());
    }
}