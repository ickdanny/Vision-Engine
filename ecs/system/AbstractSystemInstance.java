package ecs.system;

import ecs.AbstractECSInterface;

public interface AbstractSystemInstance<T> {
    void run(AbstractECSInterface ecsInterface, T data);
}