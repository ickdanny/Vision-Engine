package internalconfig.game.sliceproviders.mainmenu;

import ecs.SliceProvider;
import ecs.datastorage.AbstractDataStorageConfig;
import ecs.datastorage.AbstractSliceInitScript;
import ecs.datastorage.DataStorageConfig;
import ecs.system.SystemChainInfo;
import internalconfig.game.sliceproviders.SystemChainFactoryProvider;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.systems.Topics.*;
import static internalconfig.game.SystemChainCalls.*;

abstract class AbstractMenuSliceProvider extends SliceProvider {

    public AbstractMenuSliceProvider(String name,
                                     boolean isEntityCountStatic,
                                     int entityCount,
                                     SystemChainFactoryProvider systemChainFactoryProvider,
                                     boolean drawPrevious,
                                     AbstractSliceInitScript initScript) {
        super(
                name,
                false,
                MENU_TOPICS,
                makeDataStorageConfig(systemChainFactoryProvider, isEntityCountStatic, entityCount),
                makeSystemChainInfo(systemChainFactoryProvider, drawPrevious),
                initScript
        );
    }

    private static AbstractDataStorageConfig makeDataStorageConfig(
            SystemChainFactoryProvider systemChainFactoryProvider,
            boolean isEntityCountStatic, int entityCount) {

        int numSystems = systemChainFactoryProvider.getMenuMainSystemChainFactory().getNumSystems();
        return new DataStorageConfig(MENU_COMPONENT_TYPES.getArray(), isEntityCountStatic, entityCount, numSystems);
    }

    @SuppressWarnings("unchecked")
    private static SystemChainInfo<Double>[] makeSystemChainInfo(SystemChainFactoryProvider systemChainFactoryProvider,
                                                                 boolean drawPrevious) {
        SystemChainInfo<Double> mainInfo = new SystemChainInfo<>(
                MAIN, systemChainFactoryProvider.getMenuMainSystemChainFactory(), false);
        SystemChainInfo<Double> graphicsInfo = new SystemChainInfo<>(
                GRAPHICS, systemChainFactoryProvider.getMenuGraphicsSystemChainFactory(), drawPrevious);
        return (SystemChainInfo<Double>[]) new SystemChainInfo[]{mainInfo, graphicsInfo};
    }
}