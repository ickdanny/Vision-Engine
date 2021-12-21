package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import ecs.system.criticalorders.AddComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.SpawnComponent;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static internalconfig.game.systems.Topics.PLAYER_STATE_ENTRY;

public class PlayerReactivateSystem implements AbstractSystem<Double> {

    private final AbstractComponentType<SpawnComponent> spawnComponentType;

    public PlayerReactivateSystem(AbstractComponentTypeContainer componentTypeContainer){
        spawnComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.SpawnComponentType.class);
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double>{
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

            if(hasReactivated(sliceBoard)){
                reactivate(sliceBoard);
            }


            sliceBoard.ageAndCullMessages();
        }

        private boolean hasReactivated(AbstractPublishSubscribeBoard sliceBoard) {
            if (sliceBoard.hasTopicalMessages(PLAYER_STATE_ENTRY)) {
                List<Message<PlayerStateSystem.States>> list = sliceBoard.getMessageList(PLAYER_STATE_ENTRY);
                for (Message<PlayerStateSystem.States> message : list) {
                    if(message.getMessage() == PlayerStateSystem.States.RESPAWN_INVULNERABLE){
                        return true;
                    }
                }
            }
            return false;
        }

        private void reactivate(AbstractPublishSubscribeBoard sliceBoard){
            AddComponentOrder<SpawnComponent> addSpawnComponentOrder = new AddComponentOrder<>(
                    player, spawnComponentType, new SpawnComponent()
            );
            sliceBoard.publishMessage(ECSUtil.makeAddComponentMessage(addSpawnComponentOrder));
        }
    }
}
