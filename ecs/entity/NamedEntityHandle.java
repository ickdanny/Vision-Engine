package ecs.entity;

public class NamedEntityHandle extends EntityHandle {
    private final String name;
    public NamedEntityHandle(int entityID, int generation, String name) {
        super(entityID, generation);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return super.toString() + ": " + name;
    }
}
