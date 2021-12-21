package internalconfig.game.systems.menusystems.menucommandhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.MenuCommands;

import static internalconfig.game.components.MenuCommands.NAV_DOWN;
import static internalconfig.game.components.ComponentTypes.NeighboringElementComponentType_Down;

class NavDownCommandHandler extends AbstractNavCommandHandler {

    NavDownCommandHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer, NeighboringElementComponentType_Down.class);
    }

    @Override
    public MenuCommands getCommand() {
        return NAV_DOWN;
    }
}
