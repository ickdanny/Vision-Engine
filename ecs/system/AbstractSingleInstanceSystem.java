package ecs.system;


public abstract class AbstractSingleInstanceSystem<T> implements AbstractSystem<T>, AbstractSystemInstance<T> {
    @Override
    public final AbstractSystemInstance<T> makeInstance() {
        return this;
    }
}