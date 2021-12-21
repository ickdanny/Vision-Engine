package internalconfig.game.systems.menusystems.menucommandhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.MenuCommands;
import internalconfig.game.systems.ShotType;

import static internalconfig.game.components.ComponentTypes.*;

class NavDownAndSetShotCCommandHandler extends AbstractNavAndSetShotCommandHandler {
    NavDownAndSetShotCCommandHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer, NeighboringElementComponentType_Down.class, ShotType.C);
    }

    @Override
    public MenuCommands getCommand() {
        return MenuCommands.NAV_DOWN_AND_SET_SHOT_C;
    }
}