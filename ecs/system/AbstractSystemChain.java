package ecs.system;

public interface AbstractSystemChain<T> {
    void runSystems(T data);
}