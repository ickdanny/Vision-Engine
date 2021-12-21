package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSingleInstanceSystem;
import internalconfig.game.SliceUtil;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static internalconfig.game.systems.Topics.GAME_OVER;

public class GameOverSystem extends AbstractSingleInstanceSystem<Double> {
    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        if(sliceBoard.hasTopicalMessages(GAME_OVER)){
            List<? extends Message<?>> list = sliceBoard.getMessageList(GAME_OVER);
            int messageLifetime = ecsInterface.getSliceData().getMessageLifetime();
            for(Message<?> message : list){
                if(message.getLifetime() / messageLifetime == 0){
                    gameOver(ecsInterface, sliceBoard);
                }
            }
        }

        sliceBoard.ageAndCullMessages();
    }

    private void gameOver(AbstractECSInterface ecsInterface, AbstractPublishSubscribeBoard sliceBoard){
        switch(GameUtil.getGameMode(ecsInterface.getGlobalBoard())){
            case STORY:
            case EXTRA:
                SliceUtil.returnToMain(ecsInterface);
                break;
            case PRACTICE:
                SliceUtil.returnToMenu(ecsInterface);
                break;
        }
    }

}
