package internalconfig.game.systems.menusystems.menucommandhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.MenuCommands;
import internalconfig.game.systems.ShotType;

import static internalconfig.game.components.ComponentTypes.*;

class NavUpAndSetShotCCommandHandler extends AbstractNavAndSetShotCommandHandler {
    NavUpAndSetShotCCommandHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer, NeighboringElementComponentType_Up.class, ShotType.C);
    }

    @Override
    public MenuCommands getCommand() {
        return MenuCommands.NAV_UP_AND_SET_SHOT_C;
    }
}