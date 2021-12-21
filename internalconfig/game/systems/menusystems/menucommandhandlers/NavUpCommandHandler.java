package internalconfig.game.systems.menusystems.menucommandhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.MenuCommands;

import static internalconfig.game.components.MenuCommands.NAV_UP;
import static internalconfig.game.components.ComponentTypes.NeighboringElementComponentType_Up;

class NavUpCommandHandler extends AbstractNavCommandHandler {

    NavUpCommandHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer, NeighboringElementComponentType_Up.class);
    }

    @Override
    public MenuCommands getCommand() {
        return NAV_UP;
    }
}
