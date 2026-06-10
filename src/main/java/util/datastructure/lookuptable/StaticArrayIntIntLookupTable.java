package util.datastructure.lookuptable;

public class StaticArrayIntIntLookupTable extends AbstractArrayIntIntLookupTable {
    public StaticArrayIntIntLookupTable(int maxCapacity){
        super(maxCapacity, new UnsupportedGrowIndicesHandler(), new UnsupportedGrowValuesHandler());
    }
    public StaticArrayIntIntLookupTable(int maxIndex, int maxCapacity){
        super(maxIndex, maxCapacity, new UnsupportedGrowIndicesHandler(), new UnsupportedGrowValuesHandler());
    }
}