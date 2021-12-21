package internalconfig.game.sliceproviders.gamemenu;

import internalconfig.game.sliceproviders.SystemChainFactoryProvider;
import internalconfig.game.systems.SystemContainer;

import static internalconfig.game.systems.SliceCodes.PAUSE;
import static internalconfig.game.components.MenuCommands.BACK_TO_MAIN;

public class PauseMenuSliceProvider extends AbstractPauseMenuSliceProvider {
    public PauseMenuSliceProvider(SystemChainFactoryProvider systemChainFactoryProvider,
                                  SystemContainer systemContainer) {
        super(PAUSE, systemChainFactoryProvider, systemContainer, BACK_TO_MAIN);
    }
}
