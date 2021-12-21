package internalconfig.game.systems.gamesystems.deathhandlers;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.criticalorders.RemoveComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DeathCommands;
import internalconfig.game.components.SpawnComponent;
import internalconfig.game.components.spawns.PlayerSpawns;
import internalconfig.game.systems.PlayerData;
import internalconfig.game.systems.gamesystems.GameUtil;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.components.DeathCommands.PLAYER_DEATH;

class PlayerDeathHandler implements AbstractDeathHandler {

    private final AbstractComponentType<SpawnComponent> spawnComponentType;
    private final AbstractComponentType<Void> collidableMarker;

    PlayerDeathHandler(AbstractComponentTypeContainer componentTypeContainer){
        spawnComponentType = componentTypeContainer.getTypeInstance(SpawnComponentType.class);
        collidableMarker = componentTypeContainer.getTypeInstance(CollidableMarker.class);
    }

    @Override
    public DeathCommands getCommand() {
        return PLAYER_DEATH;
    }

    @Override
    public void handleDeath(AbstractECSInterface ecsInterface, EntityHandle deadEntity) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        removeSpawnComponent(sliceBoard, deadEntity);
        deathSpawn(dataStorage, deadEntity);
        removePower(sliceBoard);

        RemoveComponentOrder removeCollidableOrder = new RemoveComponentOrder(deadEntity, collidableMarker);
        sliceBoard.publishMessage(ECSUtil.makeRemoveComponentMessage(removeCollidableOrder));
    }

    private void removeSpawnComponent(AbstractPublishSubscribeBoard sliceBoard, EntityHandle deadEntity){

        RemoveComponentOrder removeSpawnComponentOrder = new RemoveComponentOrder(deadEntity, spawnComponentType);
        sliceBoard.publishMessage(ECSUtil.makeRemoveComponentMessage(removeSpawnComponentOrder));
    }

    private void deathSpawn(AbstractDataStorage dataStorage, EntityHandle deadEntity){
        //SpawnSystem runs after, so we use the spawnComponent right before it gets removed
        SpawnComponent spawnComponent = dataStorage.getComponent(deadEntity, spawnComponentType);
        spawnComponent.addSpawnUnit(PlayerSpawns.PLAYER_BULLET_BLOCKER);
        spawnComponent.addSpawnUnit(PlayerSpawns.PLAYER_DEATH_PICKUPS);
    }

    private void removePower(AbstractPublishSubscribeBoard sliceBoard){
        PlayerData playerData = GameUtil.getPlayerData(sliceBoard);
        int initPower = playerData.getPower();
        playerData.setPower(initPower - powerLoss(initPower));
    }

    private int powerLoss(int initPower){
        int lossRatio = 4;
        int minLoss = 4;
        int maxLoss = 16;
        if(initPower <= minLoss){
            return initPower;
        }

        int loss = (initPower / lossRatio);
        if (loss < minLoss) {
            loss = minLoss;
        } else if (loss > maxLoss) {
            loss = maxLoss;
        }
        return loss;
    }
}