package internalconfig.game.systems.menusystems.menucommandhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.MenuCommands;

import static internalconfig.game.components.MenuCommands.NAV_FAR_LEFT;
import static internalconfig.game.components.ComponentTypes.*;

class NavFarLeftCommandHandler extends AbstractNavFarCommandHandler {
    NavFarLeftCommandHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer, NeighboringElementComponentType_Left.class);
    }

    @Override
    public MenuCommands getCommand() {
        return NAV_FAR_LEFT;
    }
}
