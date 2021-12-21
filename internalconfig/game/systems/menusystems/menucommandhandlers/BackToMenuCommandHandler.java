package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.SliceUtil;
import internalconfig.game.components.MenuCommands;

import static internalconfig.game.components.MenuCommands.BACK_TO_MENU;

class BackToMenuCommandHandler implements AbstractMenuCommandHandler {

    @Override
    public MenuCommands getCommand() {
        return BACK_TO_MENU;
    }

    @Override
    public boolean handleCommand(AbstractECSInterface ecsInterface,
                                 AbstractMenuNavigationSystemInstance menuNavigationSystemInstance) {
        SliceUtil.returnToMenu(ecsInterface);
        return false;
    }
}
