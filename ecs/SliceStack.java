package ecs;

import ecs.system.AbstractSystemChainCall;
import util.messaging.PublishSubscribeBoard;
import util.messaging.Topic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SliceStack implements AbstractSliceStack {

    private final List<AbstractSlice> sliceList;
    private final Map<String, AbstractSliceProvider> sliceProviders;

    SliceStack(AbstractSliceProvider[] sliceProviders, String baseSlice, Topic<?>[] globalTopics){
        PublishSubscribeBoard globalBoard = new PublishSubscribeBoard(globalTopics);//DOES NOT HANDLE SLICE OPERATIONS
        sliceList = new ArrayList<>();
        this.sliceProviders = new HashMap<>();
        AbstractECSInterfaceFactory ecsInterfaceFactory = new ECSInterfaceFactory(this);
        for(AbstractSliceProvider sliceProvider : sliceProviders){
            sliceProvider.init(globalBoard, ecsInterfaceFactory);
            this.sliceProviders.put(sliceProvider.getName(), sliceProvider);
        }
        pushSlice(baseSlice);
    }

    @Override
    public void pushSlice(String name) {
        AbstractSliceProvider sliceProvider = sliceProviders.get(name);
        if(sliceProvider != null) {
            sliceList.add(sliceProvider.getSlice());
        }
        else{
            throw new RuntimeException("No slice provider with name: " + name);
        }
    }

    @Override
    public void popSliceBackTo(String name) {
        int i = sliceList.size() - 1;
        while (i >= 0 && !sliceList.get(i).getName().equals(name)) {
            sliceList.remove(i--);
        }
        if(sliceList.size() == 0){
            throw new RuntimeException("cannot find slice named " + name);
        }
    }

    @Override
    public void receiveSystemChainCall(AbstractSystemChainCall<?> systemChainCall) {
        if(systemChainCall.isTopDown()){
            runSystemCallOnSliceTopDown(systemChainCall, sliceList.size() - 1);
        }
        else {
            runSystemCallOnSliceBottomUp(systemChainCall, sliceList.size() - 1);
        }
    }

    private void runSystemCallOnSliceTopDown(AbstractSystemChainCall<?> systemChainCall, int index){
        AbstractSlice slice = sliceList.get(index);
        slice.runSystemChain(systemChainCall);
        if(slice.isTransparent(systemChainCall) && index > 0){
            runSystemCallOnSliceTopDown(systemChainCall, index - 1);
        }
    }

    private void runSystemCallOnSliceBottomUp(AbstractSystemChainCall<?> systemChainCall, int index){
        AbstractSlice slice = sliceList.get(index);
        if(slice.isTransparent(systemChainCall) && index > 0){
            runSystemCallOnSliceBottomUp(systemChainCall, index - 1);
        }
        slice.runSystemChain(systemChainCall);
    }
}
