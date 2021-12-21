package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.ConstPolarVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.components.spawns.DanmakuSpawns.B3_PATTERN_3_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B3_Pattern_3_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B3_Pattern_3_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                20,
                5,
                7,
                2.5,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                20,
                6,
                8,
                2,
                5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                15,
                6,
                8,
                2,
                5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                15,
                7,
                9,
                2,
                5,
                spawnBuilder
        );
    }

    protected class Template extends AbstractPositionSpawnHandler {

        private static final int SPAWN_DIST_LOW = 30;
        private static final int SPAWN_DIST_HIGH = 50;

        private final int mod;
        private final int symmetryLow;
        private final int symmetryHigh;
        private final double speedLow;
        private final double speedHigh;

        private Template(int mod,
                         int symmetryLow,
                         int symmetryHigh,
                         double speedLow,
                         double speedHigh,
                         SpawnBuilder spawnBuilder) {
            super(B3_PATTERN_3_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetryLow = symmetryLow;
            this.symmetryHigh = symmetryHigh;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random random = GameUtil.getRandom(globalBoard);
                double angle = random.nextDouble() * 360;

                double speed = RandomUtil.randDoubleInclusive(speedLow, speedHigh, random);
                int symmetry = RandomUtil.randIntInclusive(symmetryLow, symmetryHigh, random);
                double spawnDist = RandomUtil.randDoubleInclusive(SPAWN_DIST_LOW, SPAWN_DIST_HIGH, random);
                AbstractVector baseVelocity = new PolarVector(speed, angle);

                EnemyProjectileColors color = randomColor(random);

                SpawnUtil.ringFormation(baseVelocity, symmetry, (v) -> {
                    DoublePoint basePos = new ConstPolarVector(spawnDist, v.getAngle()).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, 4, (p) -> spawnBullet(p, v, color, sliceBoard));
                });
            }
        }

        private EnemyProjectileColors randomColor(Random random) {
            int index = RandomUtil.randIntInclusive(0, 5, random);
            switch (index) {
                case 0:
                    return RED;
                case 1:
                    return ORANGE;
                case 2:
                    return YELLOW;
                case 3:
                    return GREEN;
                case 4:
                    return BLUE;
                case 5:
                    return VIOLET;
                default:
                    throw new RuntimeException("unexpected index: " + index);
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 EnemyProjectileColors color,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, color, -50, 0)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}