package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.SliceUtil;
import internalconfig.game.components.MenuCommands;
import internalconfig.game.systems.GameConfigObject;
import internalconfig.game.systems.gamesystems.GameUtil;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static ecs.ECSTopics.PUSH_NEW_SLICE;
import static internalconfig.game.GlobalTopics.*;
import static internalconfig.game.systems.GameMode.STORY;
import static internalconfig.game.systems.SliceCodes.*;

class RestartGameCommandHandler implements AbstractMenuCommandHandler {
    @Override
    public MenuCommands getCommand() {
        return MenuCommands.RESTART_GAME;
    }

    @Override
    public boolean handleCommand(AbstractECSInterface ecsInterface,
                                 AbstractMenuNavigationSystemInstance menuNavigationSystemInstance) {
        AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

        GameConfigObject oldConfig = GameUtil.getGameConfigObject(globalBoard);
        List<Message<GameConfigObject>> gameConfigObjectMessageList = globalBoard.getMessageList(GAME_CONFIG_OBJECT);
        gameConfigObjectMessageList.clear();

        GameConfigObject newConfig = makeRestartGameConfigObject(oldConfig);
        gameConfigObjectMessageList.add(new Message<>(GAME_CONFIG_OBJECT, newConfig, Message.AGELESS));

        SliceUtil.returnToMenu(ecsInterface); //this handles removing the TOP_LEVEL_SLICES messages

        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        sliceBoard.publishMessage(new Message<>(PUSH_NEW_SLICE, GAME, Message.AGELESS));

        ecsInterface.getGlobalBoard().publishMessage(
                new Message<>(TOP_LEVEL_SLICES, GAME, Message.AGELESS));

        sliceBoard.publishMessage(new Message<>(PUSH_NEW_SLICE, LOAD, Message.AGELESS));

        ecsInterface.getGlobalBoard().publishMessage(
                new Message<>(TOP_LEVEL_SLICES, LOAD, Message.AGELESS));

        return true;
    }

    private GameConfigObject makeRestartGameConfigObject(GameConfigObject oldGameConfig){
        GameConfigObject newConfig = oldGameConfig;
        if(oldGameConfig.getGameMode() == STORY){
            newConfig = oldGameConfig.setStage(1);
        }
        newConfig.loadPlayerData();
        return newConfig.setRandom(System.currentTimeMillis());
    }
}