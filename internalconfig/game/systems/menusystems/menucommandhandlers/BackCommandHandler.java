package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.SliceUtil;
import internalconfig.game.components.MenuCommands;

import static internalconfig.game.components.MenuCommands.BACK;

class BackCommandHandler implements AbstractMenuCommandHandler {

    BackCommandHandler(){
    }

    @Override
    public MenuCommands getCommand() {
        return BACK;
    }

    @Override
    public boolean handleCommand(AbstractECSInterface ecsInterface,
                                 AbstractMenuNavigationSystemInstance menuNavigationSystemInstance) {

        SliceUtil.back(ecsInterface);
        return false;
    }
}
