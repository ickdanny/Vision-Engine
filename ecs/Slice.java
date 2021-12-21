package ecs;

import ecs.system.AbstractSystemChainCall;
import ecs.system.AbstractSystemChain;
import ecs.system.SystemChainInfo;

class Slice implements AbstractSlice{

    private final String name;
    private final boolean[] systemChainCallTransparency; //array over bitset - we only expect 2 chains
    private final AbstractSystemChain<?>[] systemChains;

    Slice(String name,
          SystemChainInfo<?>[] systemChainsInfo,
          AbstractECSInterface ecsInterface){

        this.name = name;

        systemChainCallTransparency = new boolean[systemChainsInfo.length];
        systemChains = new AbstractSystemChain[systemChainsInfo.length];

        for(SystemChainInfo<?> systemChainConfig: systemChainsInfo){
            int index = systemChainConfig.getCall().getIndex();
            systemChainCallTransparency[index] = systemChainConfig.isTransparent();
            systemChains[index] = systemChainConfig.getFactory().makeSystemChain(ecsInterface);
        }
    }

    @Override
    public boolean isTransparent(AbstractSystemChainCall<?> call) {
        return systemChainCallTransparency[call.getIndex()];
    }

    @Override
    public <T> void runSystemChain(AbstractSystemChainCall<T> call) {
        getSystemChain(call).runSystems(call.getData());
    }

    @SuppressWarnings("unchecked")
    private <T> AbstractSystemChain<T> getSystemChain(AbstractSystemChainCall<T> call){
        return (AbstractSystemChain<T>) systemChains[call.getIndex()];
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}

