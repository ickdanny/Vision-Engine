package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.SpawnComponent;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.game.systems.Topics.*;
import static internalconfig.game.systems.gamesystems.GameCommands.SHOOT;
import static internalconfig.game.components.spawns.PlayerSpawns.*;

public class PlayerShotSystem implements AbstractSystem<Double> {

    private final AbstractComponentType<SpawnComponent> spawnComponentType;

    public PlayerShotSystem(AbstractComponentTypeContainer componentTypeContainer){
        spawnComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.SpawnComponentType.class);
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double>{
        private EntityHandle player;
        private boolean shooting;

        public Instance() {
            shooting = false;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            if(player == null){
                player = GameUtil.getPlayer(sliceBoard);
            }

            shooting = false;
            for(Message<GameCommands> message : sliceBoard.getMessageList(GAME_COMMANDS)){
                if(message.getMessage().equals(SHOOT)){
                    shooting = true;
                    break;
                }
            }

            if(shooting){
                setPlayerShot(ecsInterface.getSliceData(), sliceBoard);
            }

            sliceBoard.ageAndCullMessages();
        }

        private void setPlayerShot(AbstractDataStorage dataStorage, AbstractPublishSubscribeBoard sliceBoard){
            if(dataStorage.containsComponent(player, spawnComponentType)){
                SpawnComponent spawnComponent = dataStorage.getComponent(player, spawnComponentType);
                if(spawnComponent.containsSpawn(SHOT)){
                    return;
                }
                spawnComponent.addSpawnUnit(SHOT);
            }
            //if player has no spawnComponent (or player doesn't exist) do nothing, no need to communicate state
        }

    }
}