package internalconfig.game.systems.menusystems.menucommandhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.MenuCommands;

import static internalconfig.game.components.MenuCommands.NAV_FAR_UP;
import static internalconfig.game.components.ComponentTypes.*;

class NavFarUpCommandHandler extends AbstractNavFarCommandHandler {
    NavFarUpCommandHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer, NeighboringElementComponentType_Up.class);
    }

    @Override
    public MenuCommands getCommand() {
        return NAV_FAR_UP;
    }
}
