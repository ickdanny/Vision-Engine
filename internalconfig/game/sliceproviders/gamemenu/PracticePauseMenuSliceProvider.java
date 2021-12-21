package internalconfig.game.sliceproviders.gamemenu;

import internalconfig.game.sliceproviders.SystemChainFactoryProvider;
import internalconfig.game.systems.SystemContainer;

import static internalconfig.game.systems.SliceCodes.PRACTICE_PAUSE;
import static internalconfig.game.components.MenuCommands.BACK_TO_MENU;

public class PracticePauseMenuSliceProvider extends AbstractPauseMenuSliceProvider {
    public PracticePauseMenuSliceProvider(SystemChainFactoryProvider systemChainFactoryProvider,
                                   SystemContainer systemContainer) {
        super(PRACTICE_PAUSE, systemChainFactoryProvider, systemContainer, BACK_TO_MENU);
    }
}
