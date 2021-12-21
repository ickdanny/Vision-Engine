package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static internalconfig.game.systems.Topics.*;

public class PlayerDeathParserSystem implements AbstractSystem<Double> {

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private static class Instance implements AbstractSystemInstance<Double>{

        private EntityHandle player;

        private Instance(){
            player = null;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            if (player == null) {
                player = GameUtil.getPlayer(sliceBoard);
            }

            if(hasDied(sliceBoard)){
                sendDeathMessage(ecsInterface, sliceBoard);
            }

            sliceBoard.ageAndCullMessages();
        }

        private boolean hasDied(AbstractPublishSubscribeBoard sliceBoard) {
            if (sliceBoard.hasTopicalMessages(PLAYER_STATE_ENTRY)) {
                List<Message<PlayerStateSystem.States>> list = sliceBoard.getMessageList(PLAYER_STATE_ENTRY);
                for (Message<PlayerStateSystem.States> message : list) {
                    if(message.getMessage() == PlayerStateSystem.States.DEAD){
                        return true;
                    }
                }
            }
            return false;
        }

        private void sendDeathMessage(AbstractECSInterface ecsInterface, AbstractPublishSubscribeBoard sliceBoard){
            sliceBoard.publishMessage(new Message<>(DEATHS, player, ecsInterface.getSliceData().getMessageLifetime()));
        }
    }
}
