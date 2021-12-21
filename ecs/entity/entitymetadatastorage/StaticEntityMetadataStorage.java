package ecs.entity.entitymetadatastorage;

import ecs.entity.EntityHandle;
import ecs.entity.EntityMetadata;

public class StaticEntityMetadataStorage implements AbstractEntityMetadataStorage {

    private final EntityMetadata[] metadataArray;
    private final AbstractFreeEntityIDStorage freeEntityIDs;

    public StaticEntityMetadataStorage(int maxCapacity){
        metadataArray = new EntityMetadata[maxCapacity];
        initMetadataArray();
        freeEntityIDs = new DynamicFreeEntityIDStorage(maxCapacity);
    }

    private void initMetadataArray(){
        for(int i = 0; i < metadataArray.length; ++i){
            metadataArray[i] = new EntityMetadata();
        }
    }

    @Override
    public int createEntity() {
        int id = freeEntityIDs.retrieveID();
        throwIfBadID(id);
        return id;
    }

    @Override
    public void reclaimEntity(int id) {
        metadataArray[id].newGeneration();
        freeEntityIDs.reclaimID(id);
    }

    @Override
    public boolean isAlive(int entityID) {
        return freeEntityIDs.isIDUsed(entityID);
    }

    @Override
    public boolean isDead(int entityID) {
        return !isAlive(entityID);
    }

    @Override
    public boolean isAlive(EntityHandle entityHandle) {
        int id = entityHandle.getEntityID();
        throwIfBadID(id);
        return freeEntityIDs.isIDUsed(id) && metadataArray[id].getGeneration() == entityHandle.getGeneration();
    }

    @Override
    public boolean isDead(EntityHandle entityHandle) {
        return !isAlive(entityHandle);
    }

    @Override
    public EntityMetadata getMetadata(int id) {
        return metadataArray[id];
        //all metadatas are init - when entity dies it just ups the generation
    }

    private void throwIfBadID(int id){
        if(id >= metadataArray.length){
            throw new RuntimeException("ID given exceeds maximum entities: " + id + " " + metadataArray.length);
        }
        else if(id < 0){
            throw new RuntimeException("ID cannot be less than 0: " + id);
        }
    }
}