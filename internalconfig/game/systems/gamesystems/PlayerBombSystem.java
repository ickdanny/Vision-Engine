package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.SpawnComponent;
import internalconfig.game.components.spawns.PlayerSpawns;
import internalconfig.game.systems.PlayerData;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.systems.Topics.*;

public class PlayerBombSystem implements AbstractSystem<Double> {

    private final AbstractComponentType<SpawnComponent> spawnComponentType;

    public PlayerBombSystem(AbstractComponentTypeContainer componentTypeContainer){
        spawnComponentType = componentTypeContainer.getTypeInstance(SpawnComponentType.class);
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double> {

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

            if(hasBombed(sliceBoard)){
                bomb(ecsInterface);
            }

            sliceBoard.ageAndCullMessages();
        }

        private void init(AbstractPublishSubscribeBoard sliceBoard){
            player = GameUtil.getPlayer(sliceBoard);
            playerData = GameUtil.getPlayerData(sliceBoard);
        }

        private boolean hasBombed(AbstractPublishSubscribeBoard sliceBoard) {
            if (sliceBoard.hasTopicalMessages(PLAYER_STATE_ENTRY)) {
                List<Message<PlayerStateSystem.States>> list = sliceBoard.getMessageList(PLAYER_STATE_ENTRY);
                for (Message<PlayerStateSystem.States> message : list) {
                    if(message.getMessage() == PlayerStateSystem.States.BOMBING){
                        return true;
                    }
                }
            }
            return false;
        }

        private void bomb(AbstractECSInterface ecsInterface){
            if(playerData.getBombs() < 1){
                throw new IllegalStateException("player has no bombs, yet PlayerStateSystem has entered BOMBING!");
            }

            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            if(dataStorage.containsComponent(player, spawnComponentType)) {
                SpawnComponent spawnComponent = dataStorage.getComponent(player, spawnComponentType);
                spawnComponent.addSpawnUnit(PlayerSpawns.BOMB);
                playerData.setBombs(playerData.getBombs() - 1);
            }
            else{
                throw new IllegalStateException("Player has no spawnComponent at bomb!");
            }
        }
    }
}