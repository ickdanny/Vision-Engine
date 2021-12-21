package ecs.entity.entitymetadatastorage;

import util.datastructure.fullintset.AbstractFullIntSet;
import util.datastructure.fullintset.BitSetFullIntSet;

class DynamicFreeEntityIDStorage implements AbstractFreeEntityIDStorage {

    private final AbstractFullIntSet fullIntSet;
    public DynamicFreeEntityIDStorage(int initCapacity){
        fullIntSet = new BitSetFullIntSet(initCapacity);
    }

    @Override
    public boolean isIDUsed(int id) {
        return !fullIntSet.contains(id);
    }

    @Override
    public int retrieveID() {
        return fullIntSet.retrieveNextInt();
    }

    @Override
    public void reclaimID(int id) {
        if(!fullIntSet.addBack(id)){
            throw new RuntimeException();
        }
    }
}