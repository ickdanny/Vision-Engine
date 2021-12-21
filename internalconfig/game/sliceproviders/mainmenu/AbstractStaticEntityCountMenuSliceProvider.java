package internalconfig.game.sliceproviders.mainmenu;

import ecs.datastorage.AbstractSliceInitScript;
import internalconfig.game.sliceproviders.SystemChainFactoryProvider;

abstract class AbstractStaticEntityCountMenuSliceProvider extends AbstractMenuSliceProvider {
    public AbstractStaticEntityCountMenuSliceProvider(String name,
                                                      int entityCount,
                                                      SystemChainFactoryProvider systemChainFactoryProvider,
                                                      boolean drawPrevious,
                                                      AbstractSliceInitScript initScript) {
        super(name, true, entityCount, systemChainFactoryProvider, drawPrevious, initScript);
    }
}
