package internalconfig.game.systems.gamesystems.spawnhandlers.deathspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

class AbstractDropSmallPowerChanceSpawnHandler extends AbstractEnemyDeathSpawnHandler {

    private final double chance;

    AbstractDropSmallPowerChanceSpawnHandler(Spawns spawn,
                                             SpawnBuilder spawnBuilder,
                                             AbstractComponentTypeContainer componentTypeContainer,
                                             double chance) {
        super(spawn, spawnBuilder, componentTypeContainer);
        this.chance = chance;
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        super.handleSpawn(ecsInterface, tick, entityID);
        Random random = GameUtil.getRandom(ecsInterface.getGlobalBoard());
        if (random.nextDouble() < chance) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            SpawnUtil.PickupSpawner.spawnSmallPowerPickup(sliceBoard, spawnBuilder, pos);
        }
    }
}
