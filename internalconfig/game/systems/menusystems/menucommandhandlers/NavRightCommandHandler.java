package internalconfig.game.systems.menusystems.menucommandhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.MenuCommands;

import static internalconfig.game.components.MenuCommands.NAV_RIGHT;
import static internalconfig.game.components.ComponentTypes.NeighboringElementComponentType_Right;

class NavRightCommandHandler extends AbstractNavCommandHandler {

    NavRightCommandHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer, NeighboringElementComponentType_Right.class);
    }

    @Override
    public MenuCommands getCommand() {
        return NAV_RIGHT;
    }
}
