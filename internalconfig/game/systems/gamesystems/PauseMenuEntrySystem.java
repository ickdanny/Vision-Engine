package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSingleInstanceSystem;
import internalconfig.game.systems.GameMode;
import internalconfig.game.systems.SliceCodes;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static ecs.ECSTopics.PUSH_NEW_SLICE;
import static internalconfig.game.GlobalTopics.TOP_LEVEL_SLICES;
import static internalconfig.game.systems.Topics.GAME_COMMANDS;
import static internalconfig.game.systems.Topics.PAUSE_STATE;

public class PauseMenuEntrySystem extends AbstractSingleInstanceSystem<Double> {
    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        for(Message<GameCommands> message : sliceBoard.getMessageList(GAME_COMMANDS)){
            if(message.getMessage() == GameCommands.PAUSE){
                String pauseSliceCode = getPauseSliceCode(ecsInterface);
                sliceBoard.publishMessage(new Message<>(PUSH_NEW_SLICE, pauseSliceCode, Message.AGELESS));
                ecsInterface.getGlobalBoard().publishMessage(
                        new Message<>(TOP_LEVEL_SLICES, pauseSliceCode, Message.AGELESS));
                sliceBoard.publishMessage(new Message<>(PAUSE_STATE, null, Message.AGELESS));
            }
        }

        sliceBoard.ageAndCullMessages();
    }

    private static String getPauseSliceCode(AbstractECSInterface ecsInterface){
        AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
        return GameUtil.getGameMode(globalBoard) == GameMode.PRACTICE
                ? SliceCodes.PRACTICE_PAUSE
                : SliceCodes.PAUSE;
    }
}
