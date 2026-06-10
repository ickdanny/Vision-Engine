package util.datastructure.lookuptable;


public class DynamicArrayIntLookupTable<E> extends AbstractArrayIntLookupTable<E> {
    public DynamicArrayIntLookupTable(int initCapacity){
        super(initCapacity, new GrowIndicesHandler(), new GrowValuesHandler());
    }
    public DynamicArrayIntLookupTable(int initIndexCapacity, int initValueCapacity){
        super(initIndexCapacity, initValueCapacity, new GrowIndicesHandler(), new GrowValuesHandler());
    }
}