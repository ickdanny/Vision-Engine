package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSingleInstanceSystem;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.systems.Topics.*;

public class GameStateSystem extends AbstractSingleInstanceSystem<Double> {
    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        if(sliceBoard.hasTopicalMessages(PAUSE_STATE)){
            sliceBoard.getMessageList(PAUSE_STATE).clear();
        }
        sliceBoard.ageAndCullMessages();
    }
}