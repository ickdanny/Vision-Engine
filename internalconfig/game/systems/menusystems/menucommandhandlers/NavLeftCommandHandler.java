package internalconfig.game.systems.menusystems.menucommandhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.MenuCommands;

import static internalconfig.game.components.MenuCommands.NAV_LEFT;
import static internalconfig.game.components.ComponentTypes.NeighboringElementComponentType_Left;

class NavLeftCommandHandler extends AbstractNavCommandHandler {

    NavLeftCommandHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer, NeighboringElementComponentType_Left.class);
    }

    @Override
    public MenuCommands getCommand() {
        return NAV_LEFT;
    }
}
