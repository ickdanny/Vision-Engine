package internalconfig.game.systems.gamesystems.deathhandlers;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.criticalorders.RemoveEntityOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DeathCommands;
import internalconfig.game.components.PickupTypes;
import internalconfig.game.components.SpawnComponent;
import internalconfig.game.components.spawns.OtherGameSpawns;
import internalconfig.game.systems.PlayerData;
import internalconfig.game.systems.gamesystems.GameUtil;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.components.DeathCommands.PICKUP_DEATH;

class PickupDeathHandler implements AbstractDeathHandler {

    private final AbstractComponentType<PickupTypes> pickupTypeComponentType;
    private final AbstractComponentType<Integer> pickupDataComponentType;

    private final AbstractComponentType<SpawnComponent> spawnComponentType;

    PickupDeathHandler(AbstractComponentTypeContainer componentTypeContainer){
        pickupTypeComponentType = componentTypeContainer.getTypeInstance(PickupTypeComponentType.class);
        pickupDataComponentType = componentTypeContainer.getTypeInstance(PickupDataComponentType.class);

        spawnComponentType = componentTypeContainer.getTypeInstance(SpawnComponentType.class);
    }

    @Override
    public DeathCommands getCommand() {
        return PICKUP_DEATH;
    }

    @Override
    public void handleDeath(AbstractECSInterface ecsInterface, EntityHandle deadEntity) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        if(dataStorage.containsComponent(deadEntity, pickupTypeComponentType)) {
            PickupTypes pickupType = dataStorage.getComponent(deadEntity, pickupTypeComponentType);
            PlayerData playerData = GameUtil.getPlayerData(sliceBoard);
            switchOnPickupType(sliceBoard, dataStorage, deadEntity, pickupType, playerData);
        }
        else{
            throw new IllegalStateException(deadEntity + " has PickupDeath but no PickupType!");
        }

        sliceBoard.publishMessage(ECSUtil.makeRemoveEntityMessage(new RemoveEntityOrder(deadEntity)));
    }

    private void switchOnPickupType(AbstractPublishSubscribeBoard sliceBoard,
                                    AbstractDataStorage dataStorage,
                                    EntityHandle deadEntity,
                                    PickupTypes pickupType,
                                    PlayerData playerData){
        switch(pickupType){
            case LIFE:
                handleLife(playerData);
                break;
            case BOMB:
                handleBomb(playerData);
                break;
            case POWER:
                handlePower(sliceBoard, dataStorage, deadEntity, playerData);
                break;
        }
    }

    private void handleLife(PlayerData playerData){
        if(playerData.getLives() < MAX_LIVES) {
            playerData.setLives(playerData.getLives() + 1);
        }
    }

    private void handleBomb(PlayerData playerData){
        if(playerData.getBombs() < MAX_BOMBS) {
            playerData.setBombs(playerData.getBombs() + 1);
        }
    }

    private void handlePower(AbstractPublishSubscribeBoard sliceBoard,
                             AbstractDataStorage dataStorage,
                             EntityHandle deadEntity,
                             PlayerData playerData){
        if(dataStorage.containsComponent(deadEntity, pickupDataComponentType)){
            if(playerData.getPower() < MAX_POWER){
                int powerGain = dataStorage.getComponent(deadEntity, pickupDataComponentType);
                playerData.setPower(Math.min(playerData.getPower() + powerGain, MAX_POWER));
                if(playerData.getPower() == MAX_POWER){
                    handleMaxPower(sliceBoard, dataStorage);
                }
            }
        }
        else{
            throw new IllegalStateException(deadEntity + " is power type but no pickup data!");
        }
    }

    private void handleMaxPower(AbstractPublishSubscribeBoard sliceBoard, AbstractDataStorage dataStorage){
        EntityHandle spawner = GameUtil.getSpawner(sliceBoard);
        SpawnComponent spawnComponent = dataStorage.getComponent(spawner, spawnComponentType);
        spawnComponent.addSpawnUnit(OtherGameSpawns.BULLET_CLEAR);
    }
}