package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.SliceUtil;
import internalconfig.game.components.MenuCommands;

import static internalconfig.game.components.MenuCommands.BACK_TO_MAIN;

class BackToMainCommandHandler implements AbstractMenuCommandHandler {
    @Override
    public MenuCommands getCommand() {
        return BACK_TO_MAIN;
    }

    @Override
    public boolean handleCommand(AbstractECSInterface ecsInterface,
                                 AbstractMenuNavigationSystemInstance menuNavigationSystemInstance) {
        SliceUtil.returnToMain(ecsInterface);
        return false;
    }
}