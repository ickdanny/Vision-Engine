package internalconfig.game.sliceproviders.gamemenu;

import ecs.datastorage.AbstractSliceInitScript;
import internalconfig.game.sliceproviders.SystemChainFactoryProvider;

class AbstractStaticEntityCountGameMenuSliceProvider extends AbstractGameMenuSliceProvider {
    AbstractStaticEntityCountGameMenuSliceProvider(String name,
                                                   int entityCount,
                                                   SystemChainFactoryProvider systemChainFactoryProvider,
                                                   AbstractSliceInitScript initScript) {
        super(name, true, entityCount, systemChainFactoryProvider, initScript);
    }
}
