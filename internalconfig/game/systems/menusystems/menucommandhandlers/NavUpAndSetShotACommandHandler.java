package internalconfig.game.systems.menusystems.menucommandhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.MenuCommands;
import internalconfig.game.systems.ShotType;

import static internalconfig.game.components.ComponentTypes.*;

class NavUpAndSetShotACommandHandler extends AbstractNavAndSetShotCommandHandler {
    NavUpAndSetShotACommandHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer, NeighboringElementComponentType_Up.class, ShotType.A);
    }

    @Override
    public MenuCommands getCommand() {
        return MenuCommands.NAV_UP_AND_SET_SHOT_A;
    }
}