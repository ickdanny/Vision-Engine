package ecs.entity.entitymetadatastorage;

interface AbstractFreeEntityIDStorage {
    boolean isIDUsed(int id);
    int retrieveID();
    void reclaimID(int id);
}