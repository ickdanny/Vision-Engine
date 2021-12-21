package ecs.system;

import ecs.AbstractECSInterface;

public class SystemChainFactory<T> implements AbstractSystemChainFactory<T> {

    private final AbstractSystem<T>[] systems;

    public SystemChainFactory(AbstractSystem<T>[] systems){
        this.systems = systems;
    }

    @Override
    public AbstractSystemChain<T> makeSystemChain(AbstractECSInterface ecsInterface) {
        return new SystemChain<>(makeSystemInstances(), ecsInterface);
    }

    @SuppressWarnings("unchecked")
    private AbstractSystemInstance<T>[] makeSystemInstances(){
        AbstractSystemInstance<T>[] toRet = (AbstractSystemInstance<T>[])new AbstractSystemInstance[systems.length];
        for(int i = 0; i < systems.length; ++i){
            toRet[i] = systems[i].makeInstance();
        }
        return toRet;
    }

    @Override
    public int getNumSystems() {
        return systems.length;
    }
}