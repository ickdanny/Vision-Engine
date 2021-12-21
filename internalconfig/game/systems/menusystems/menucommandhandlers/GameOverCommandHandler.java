package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.SliceUtil;
import internalconfig.game.components.MenuCommands;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.game.components.MenuCommands.GAME_OVER;
import static internalconfig.game.GlobalTopics.*;

class GameOverCommandHandler implements AbstractMenuCommandHandler {

    GameOverCommandHandler(){}

    @Override
    public MenuCommands getCommand() {
        return GAME_OVER;
    }

    @Override
    public boolean handleCommand(AbstractECSInterface ecsInterface,
                                 AbstractMenuNavigationSystemInstance menuNavigationSystemInstance) {
        AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
        globalBoard.publishMessage(new Message<>(CONTINUE_SCREEN_GAME_OVER, null, Message.AGELESS));
        SliceUtil.back(ecsInterface);
        return false;
    }
}
