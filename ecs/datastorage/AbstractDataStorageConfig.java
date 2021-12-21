package ecs.datastorage;

import ecs.component.AbstractComponentType;

public interface AbstractDataStorageConfig {
    AbstractComponentType<?>[] getTypes();
    boolean isEntityCountStatic();
    int entityCount();
    int baseMessageLifetime();
}