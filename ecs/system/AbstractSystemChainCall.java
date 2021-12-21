package ecs.system;

public interface AbstractSystemChainCall<T> {
    int getIndex();
    boolean isTopDown();
    T getData();
}