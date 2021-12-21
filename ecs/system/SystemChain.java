package ecs.system;

import ecs.AbstractECSInterface;

class SystemChain<T> implements AbstractSystemChain<T> {
    private final AbstractSystemInstance<T>[] systemInstances;
    private final AbstractECSInterface ecsInterface;

    SystemChain(AbstractSystemInstance<T>[] systemInstances, AbstractECSInterface ecsInterface) {
        this.systemInstances = systemInstances;
        this.ecsInterface = ecsInterface;
    }

    @Override
    public void runSystems(T data) {
        for(AbstractSystemInstance<T> systemInstance: systemInstances){
            systemInstance.run(ecsInterface, data);
        }
    }
}