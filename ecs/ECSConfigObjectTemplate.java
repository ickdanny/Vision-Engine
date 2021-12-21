package ecs;

import util.messaging.Topic;

public class ECSConfigObjectTemplate implements AbstractECSConfigObject {

    private final AbstractSliceProvider[] sliceProviders;
    private final String baseSlice;
    private final Topic<?>[] globalTopics;

    public ECSConfigObjectTemplate(AbstractSliceProvider[] sliceProviders, String baseSlice, Topic<?>[] globalTopics) {
        this.sliceProviders = sliceProviders;
        this.baseSlice = baseSlice;
        this.globalTopics = globalTopics;
    }

    @Override
    public AbstractSliceProvider[] getSliceProviders() {
        return sliceProviders;
    }

    @Override
    public String getBaseSlice() {
        return baseSlice;
    }

    @Override
    public Topic<?>[] getGlobalTopics() {
        return globalTopics;
    }
}
