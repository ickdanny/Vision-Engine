package internalconfig.game.systems.menusystems.menucommandhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.MenuCommands;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.components.MenuCommands.NAV_FAR_DOWN;

class NavFarDownCommandHandler extends AbstractNavFarCommandHandler {
    NavFarDownCommandHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer, NeighboringElementComponentType_Down.class);
    }

    @Override
    public MenuCommands getCommand() {
        return NAV_FAR_DOWN;
    }
}
