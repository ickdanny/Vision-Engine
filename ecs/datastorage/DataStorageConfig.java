package ecs.datastorage;

import ecs.component.AbstractComponentType;

public class DataStorageConfig implements AbstractDataStorageConfig {

    private final AbstractComponentType<?>[] componentTypes;
    private final boolean isEntityCountStatic;
    private final int entityCount;
    private final int numSystems;

    public DataStorageConfig(AbstractComponentType<?>[] componentTypes,
                             boolean isEntityCountStatic, int entityCount, int numSystems) {
        this.componentTypes = componentTypes;
        this.isEntityCountStatic = isEntityCountStatic;
        this.entityCount = entityCount;
        this.numSystems = numSystems;
    }

    @Override
    public AbstractComponentType<?>[] getTypes() {
        return componentTypes;
    }

    @Override
    public boolean isEntityCountStatic() {
        return isEntityCountStatic;
    }

    @Override
    public int entityCount() {
        return entityCount;
    }

    @Override
    public int baseMessageLifetime() {
        return numSystems;
    }
}
