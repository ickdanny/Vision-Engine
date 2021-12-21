package internalconfig.game.systems.gamesystems.spawnhandlers.deathspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

public class DropSmallPowerSpawnHandler extends AbstractEnemyDeathSpawnHandler {

    public DropSmallPowerSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(DeathSpawns.DROP_SMALL_POWER, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        super.handleSpawn(ecsInterface, tick, entityID);
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

        SpawnUtil.PickupSpawner.spawnSmallPowerPickup(sliceBoard, spawnBuilder, pos);
    }
}