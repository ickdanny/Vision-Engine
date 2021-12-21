package ecs.entity.entitymetadatastorage;

import ecs.entity.EntityHandle;
import ecs.entity.EntityMetadata;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class DynamicEntityMetadataStorage implements AbstractEntityMetadataStorage{

    private static final int INIT_METADATA_BUFFER_SIZE = 50;

    private final List<EntityMetadata> metadataList;
    private final AbstractFreeEntityIDStorage freeEntityIDs;

    public DynamicEntityMetadataStorage(int initCapacity){
        metadataList = new ArrayList<>(initCapacity);
        initMetadataTo(initCapacity - 1);
        freeEntityIDs = new DynamicFreeEntityIDStorage(initCapacity);
    }

    private void initMetadataTo(int indexInclusive){
        for(int i = metadataList.size(); i <= indexInclusive + INIT_METADATA_BUFFER_SIZE; ++i){
            metadataList.add(new EntityMetadata());
        }
    }

    @Override
    public int createEntity() {
        return freeEntityIDs.retrieveID();
    }

    @Override
    public void reclaimEntity(int id) {
        metadataList.get(id).newGeneration();
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
        return freeEntityIDs.isIDUsed(id) && getMetadata(id).getGeneration() == entityHandle.getGeneration();
    }

    @Override
    public boolean isDead(EntityHandle entityHandle) {
        return !isAlive(entityHandle);
    }

    @Override
    public EntityMetadata getMetadata(int id) {
        if(id >= metadataList.size()){
            initMetadataTo(id);
        }
        return metadataList.get(id);
    }
}