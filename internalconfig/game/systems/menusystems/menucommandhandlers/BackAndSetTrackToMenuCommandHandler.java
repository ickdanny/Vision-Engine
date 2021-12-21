package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.SliceUtil;
import internalconfig.game.components.MenuCommands;
import util.messaging.Message;

import static internalconfig.game.GlobalTopics.RETURN_TO_MENU;

import static internalconfig.game.components.MenuCommands.BACK_AND_SET_TRACK_TO_MENU;

class BackAndSetTrackToMenuCommandHandler implements AbstractMenuCommandHandler {

    BackAndSetTrackToMenuCommandHandler(){
    }

    @Override
    public MenuCommands getCommand() {
        return BACK_AND_SET_TRACK_TO_MENU;
    }

    @Override
    public boolean handleCommand(AbstractECSInterface ecsInterface,
                                 AbstractMenuNavigationSystemInstance menuNavigationSystemInstance) {
        SliceUtil.back(ecsInterface);
        ecsInterface.getGlobalBoard().publishMessage(new Message<>(RETURN_TO_MENU, null, Message.AGELESS));
        return false;
    }
}
