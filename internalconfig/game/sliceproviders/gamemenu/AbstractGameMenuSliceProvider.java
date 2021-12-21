package internalconfig.game.sliceproviders.gamemenu;

import ecs.SliceProvider;
import ecs.datastorage.AbstractDataStorageConfig;
import ecs.datastorage.AbstractSliceInitScript;
import ecs.datastorage.DataStorageConfig;
import ecs.system.SystemChainInfo;
import internalconfig.game.sliceproviders.SystemChainFactoryProvider;

import static internalconfig.game.SystemChainCalls.GRAPHICS;
import static internalconfig.game.SystemChainCalls.MAIN;
import static internalconfig.game.components.ComponentTypes.MENU_COMPONENT_TYPES;
import static internalconfig.game.systems.Topics.MENU_TOPICS;

@SuppressWarnings("SameParameterValue")
abstract class AbstractGameMenuSliceProvider extends SliceProvider {

    AbstractGameMenuSliceProvider(String name,
                                  boolean isEntityCountStatic,
                                  int entityCount,
                                  SystemChainFactoryProvider systemChainFactoryProvider,
                                  AbstractSliceInitScript initScript) {
        super(name, true, MENU_TOPICS,
                makeDataStorageConfig(systemChainFactoryProvider, isEntityCountStatic, entityCount),
                makeSystemChainInfo(systemChainFactoryProvider), initScript);
    }

    private static AbstractDataStorageConfig makeDataStorageConfig(
            SystemChainFactoryProvider systemChainFactoryProvider,
            boolean isEntityCountStatic, int entityCount){

        int numSystems = systemChainFactoryProvider.getPauseMainSystemChainFactory().getNumSystems();
        //careful on the menuComponentTypes here
        return new DataStorageConfig(MENU_COMPONENT_TYPES.getArray(), isEntityCountStatic, entityCount, numSystems);
    }

    @SuppressWarnings("unchecked")
    private static SystemChainInfo<Double>[] makeSystemChainInfo(SystemChainFactoryProvider systemChainFactoryProvider){
        SystemChainInfo<Double> mainInfo = new SystemChainInfo<>(
                MAIN, systemChainFactoryProvider.getPauseMainSystemChainFactory(), false);
        SystemChainInfo<Double> graphicsInfo = new SystemChainInfo<>(
                GRAPHICS, systemChainFactoryProvider.getPauseGraphicsSystemChainFactory(), true);
        return (SystemChainInfo<Double>[])new SystemChainInfo[]{mainInfo, graphicsInfo};
    }
}