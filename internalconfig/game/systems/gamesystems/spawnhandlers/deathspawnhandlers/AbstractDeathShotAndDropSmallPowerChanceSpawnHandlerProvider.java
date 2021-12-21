package internalconfig.game.systems.gamesystems.spawnhandlers.deathspawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

@SuppressWarnings("unused")
class AbstractDeathShotAndDropSmallPowerChanceSpawnHandlerProvider extends AbstractDeathShotSpawnHandlerProvider {

    private final double chance;

    public AbstractDeathShotAndDropSmallPowerChanceSpawnHandlerProvider(Spawns spawn,
                                                                        SpawnBuilder spawnBuilder,
                                                                        AbstractComponentTypeContainer componentTypeContainer,
                                                                        double chance) {
        super(spawn, spawnBuilder, componentTypeContainer);
        this.chance = chance;
    }

    @Override
    protected void furtherSpawn(AbstractECSInterface ecsInterface, int tick, int entityID, DoublePoint pos) {
        Random random = GameUtil.getRandom(ecsInterface.getGlobalBoard());
        if (random.nextDouble() < chance) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            SpawnUtil.PickupSpawner.spawnSmallPowerPickup(sliceBoard, spawnBuilder, pos);
        }
    }
}
