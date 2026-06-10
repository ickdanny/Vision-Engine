package util.datastructure.fullintset;

import java.util.BitSet;

public class BitSetFullIntSet implements AbstractFullIntSet {
    private final BitSet bitSet;

    public BitSetFullIntSet(int initCapacity){
        bitSet = new BitSet(initCapacity);
    }

    @Override
    public boolean contains(int i) {
        return !bitSet.get(i); //check i = false
    }

    @Override
    public boolean retrieve(int i) {
        boolean prev = bitSet.get(i);
        if(!prev){
            //if previously the bitSet contained i i.e. i = false, then we return true to show it DID contain the int
            bitSet.set(i);
            return true;
        }
        return false;
    }

    @Override
    public boolean addBack(int i) {
        boolean prev = bitSet.get(i);
        if(!prev){
            //if previously the bitSet contained i i.e. i = false, then we return false to show it DID contain the int
            return false;
        }
        bitSet.clear(i);
        return true;
    }

    @SuppressWarnings("unused")
    @Override
    public void addAllBack() {
        bitSet.clear();
    }

    @Override
    public int retrieveNextInt(){
        int nextInt = bitSet.nextClearBit(0);
        if(!retrieve(nextInt)){
            throw new RuntimeException();
        }
        return nextInt;
    }
}