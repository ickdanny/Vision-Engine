package internalconfig.game;

import ecs.ECSConfigObjectTemplate;
import internalconfig.game.sliceproviders.SliceProviderContainer;

class ECSConfigObject extends ECSConfigObjectTemplate {
    public ECSConfigObject(SliceProviderContainer sliceProviderContainer){
        super(sliceProviderContainer.getSliceProviders(),
              SliceProviderContainer.BASE_SLICE,
              GlobalTopics.GLOBAL_TOPICS);
    }
}
