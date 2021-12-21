package internalconfig.game.systems.gamesystems.spawnhandlers.pickupspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.PICKUP_INBOUND;

abstract class AbstractBossBigPowerDropSpawnHandler extends AbstractPositionSpawnHandler {

    private static final double SMALL_POWER_RADIUS = 60;
    private static final double RADIUS_AWAY_FROM_LARGE_POWER = 20;

    private final int smallPowers;

    AbstractBossBigPowerDropSpawnHandler(Spawns spawn,
                                         SpawnBuilder spawnBuilder,
                                         AbstractComponentTypeContainer componentTypeContainer,
                                         int smallPowers) {
        super(spawn, spawnBuilder, componentTypeContainer);
        this.smallPowers = smallPowers;
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        Random random = GameUtil.getRandom(ecsInterface.getGlobalBoard());
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

        SpawnUtil.PickupSpawner.spawnLargePowerPickup(sliceBoard, spawnBuilder, pos);

        double speed = SpawnUtil.pickupInitSpeed(GameUtil.inboundPosition(pos, PICKUP_INBOUND));

        for (int i = 0; i < smallPowers; ++i) {
            DoublePoint tryPos = generateRandomPositionInRadius(pos, random);
            while (GameUtil.isOutOfBounds(tryPos, PICKUP_INBOUND)
                    || Math.abs(pos.getX() - tryPos.getX()) < RADIUS_AWAY_FROM_LARGE_POWER
                    || Math.abs(pos.getY() - tryPos.getY()) < RADIUS_AWAY_FROM_LARGE_POWER) {
                tryPos = generateRandomPositionInRadius(pos, random);
            }
            SpawnUtil.PickupSpawner.spawnSmallPowerPickup(sliceBoard, spawnBuilder, tryPos, speed);
        }
    }

    private DoublePoint generateRandomPositionInRadius(DoublePoint pos, Random random) {
        return new DoublePoint(
                pos.getX() + RandomUtil.randDoubleInclusive(-SMALL_POWER_RADIUS, SMALL_POWER_RADIUS, random),
                pos.getY() + RandomUtil.randDoubleInclusive(-SMALL_POWER_RADIUS, SMALL_POWER_RADIUS, random)
        );
    }
}