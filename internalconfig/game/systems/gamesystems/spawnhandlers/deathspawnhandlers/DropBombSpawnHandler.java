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

public class DropBombSpawnHandler extends AbstractEnemyDeathSpawnHandler {
    public DropBombSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(DeathSpawns.DROP_BOMB, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        super.handleSpawn(ecsInterface, tick, entityID);
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

        SpawnUtil.PickupSpawner.spawnBombPickup(sliceBoard, spawnBuilder, pos);
    }
}