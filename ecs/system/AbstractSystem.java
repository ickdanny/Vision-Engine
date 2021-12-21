package ecs.system;

public interface AbstractSystem<T> {
    AbstractSystemInstance<T> makeInstance();
}