package ecs.entity.entitymetadatastorage;

import ecs.entity.EntityHandle;
import ecs.entity.EntityMetadata;

public interface AbstractEntityMetadataStorage {
    int createEntity();
    void reclaimEntity(int id);
    boolean isAlive(int entityID);
    boolean isDead(int entityID);
    boolean isAlive(EntityHandle entityHandle);
    boolean isDead(EntityHandle entityHandle);
    EntityMetadata getMetadata(int id);
}