package ecs.entity;

import java.util.Objects;

public class EntityHandle {
    private final int entityID;
    private final int generation;

    public EntityHandle(int entityID, int generation) {
        this.entityID = entityID;
        this.generation = generation;
    }

    public int getEntityID() {
        return entityID;
    }

    public int getGeneration() {
        return generation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityHandle)) return false;
        EntityHandle that = (EntityHandle) o;
        return entityID == that.entityID &&
                generation == that.generation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityID, generation);
    }

    @Override
    public String toString() {
        return "E_" + entityID + " G_" + generation;
    }
}