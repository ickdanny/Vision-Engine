package internalconfig.game.systems.menusystems.menucommandhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.MenuCommands;

import static internalconfig.game.components.MenuCommands.NAV_FAR_RIGHT;
import static internalconfig.game.components.ComponentTypes.*;

class NavFarRightCommandHandler extends AbstractNavFarCommandHandler {
    NavFarRightCommandHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer, NeighboringElementComponentType_Right.class);
    }

    @Override
    public MenuCommands getCommand() {
        return NAV_FAR_RIGHT;
    }
}
