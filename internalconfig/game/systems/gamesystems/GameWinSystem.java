package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSingleInstanceSystem;
import internalconfig.game.systems.SliceCodes;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static ecs.ECSTopics.PUSH_NEW_SLICE;
import static internalconfig.game.GlobalTopics.TOP_LEVEL_SLICES;
import static internalconfig.game.systems.Topics.GAME_WIN;

public class GameWinSystem extends AbstractSingleInstanceSystem<Double> {
    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        if(sliceBoard.hasTopicalMessages(GAME_WIN)){
            List<? extends Message<?>> list = sliceBoard.getMessageList(GAME_WIN);
            int messageLifetime = ecsInterface.getSliceData().getMessageLifetime();
            for(Message<?> message : list){
                if(message.getLifetime() / messageLifetime == 0){
                    gameWin(ecsInterface, sliceBoard);
                }
            }
        }

        sliceBoard.ageAndCullMessages();
    }

    private void gameWin(AbstractECSInterface ecsInterface, AbstractPublishSubscribeBoard sliceBoard){
        AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

        sliceBoard.publishMessage(new Message<>(PUSH_NEW_SLICE, SliceCodes.CREDITS, Message.AGELESS));
        globalBoard.publishMessage(new Message<>(TOP_LEVEL_SLICES, SliceCodes.CREDITS, Message.AGELESS));
    }

}
