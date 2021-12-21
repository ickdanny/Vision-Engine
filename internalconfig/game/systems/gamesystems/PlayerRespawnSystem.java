package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import ecs.system.criticalorders.AddComponentOrder;
import ecs.system.criticalorders.SetComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.PlayerData;
import util.math.geometry.TwoFramePosition;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.systems.Topics.*;
import static internalconfig.game.components.ComponentTypes.*;

public class PlayerRespawnSystem implements AbstractSystem<Double> {

    private final AbstractComponentType<TwoFramePosition> positionComponentType;
    private final AbstractComponentType<Void> collidableMarker;

    public PlayerRespawnSystem(AbstractComponentTypeContainer componentTypeContainer){
        positionComponentType = componentTypeContainer.getTypeInstance(PositionComponentType.class);
        collidableMarker = componentTypeContainer.getTypeInstance(CollidableMarker.class);
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double>{
        private EntityHandle player;
        private PlayerData playerData;

        private Instance(){
            player = null;
            playerData = null;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            if (player == null) {
                init(sliceBoard);
            }

            if(hasRespawned(sliceBoard)){
                respawn(sliceBoard);
            }


            sliceBoard.ageAndCullMessages();
        }

        private void init(AbstractPublishSubscribeBoard sliceBoard){
            player = GameUtil.getPlayer(sliceBoard);
            playerData = GameUtil.getPlayerData(sliceBoard);
        }

        private boolean hasRespawned(AbstractPublishSubscribeBoard sliceBoard) {
            if (sliceBoard.hasTopicalMessages(PLAYER_STATE_ENTRY)) {
                List<Message<PlayerStateSystem.States>> list = sliceBoard.getMessageList(PLAYER_STATE_ENTRY);
                for (Message<PlayerStateSystem.States> message : list) {
                    if(message.getMessage() == PlayerStateSystem.States.RESPAWNING){
                        return true;
                    }
                }
            }
            return false;
        }

        private void respawn(AbstractPublishSubscribeBoard sliceBoard){
            AddComponentOrder<Void> addCollidableOrder = new AddComponentOrder<>(
                    player, collidableMarker, null
            );
            sliceBoard.publishMessage(ECSUtil.makeAddComponentMessage(addCollidableOrder));

            SetComponentOrder<TwoFramePosition> setPositionOrder = new SetComponentOrder<>(
                    player, positionComponentType, new TwoFramePosition(PLAYER_SPAWN)
            );
            sliceBoard.publishMessage(ECSUtil.makeSetComponentMessage(setPositionOrder));

            playerData.setBombs(RESPAWN_BOMBS);

            if(!sliceBoard.hasTopicalMessages(CONTINUE_USED)){
                if(playerData.getLives() <= 0){
                    throw new RuntimeException("did not catch continue when lives <= 0 to start!");
                }
                playerData.setLives(playerData.getLives() - 1);
            }else{
                playerData.setLives(CONTINUE_LIVES);
                playerData.setContinues(playerData.getContinues() - 1);
            }
        }
    }
}