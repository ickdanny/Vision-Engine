package internalconfig.game.systems.gamesystems.spawnhandlers.deathspawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

@SuppressWarnings("unused")
public class DeathShotAndDropSmallPowerHandlerProvider extends AbstractDeathShotSpawnHandlerProvider {

    public DeathShotAndDropSmallPowerHandlerProvider(SpawnBuilder spawnBuilder,
                                                     AbstractComponentTypeContainer componentTypeContainer) {
        super(DeathSpawns.DEATH_SHOT_AND_DROP_SMALL_POWER, spawnBuilder, componentTypeContainer);
    }

    @Override
    protected void furtherSpawn(AbstractECSInterface ecsInterface, int tick, int entityID, DoublePoint pos) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        SpawnUtil.PickupSpawner.spawnSmallPowerPickup(sliceBoard, spawnBuilder, pos);
    }
}
