package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B3_PATTERN_2_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B3_Pattern_2_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B3_Pattern_2_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                30,
                6,
                21,
                42,
                10,
                2,
                3.5,
                6,
                9,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                27,
                7,
                31,
                42,
                9,
                2,
                3.5,
                8,
                11,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                23,
                9,
                35,
                42,
                8,
                2,
                3.5,
                11,
                13,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                20,
                11,
                41,
                42,
                7,
                2,
                3.5,
                13,
                15,
                spawnBuilder
        );
    }

    private class Template extends AbstractPositionSpawnHandler {

        private static final double MEDIUM_SPEED = 4.12;
        private static final double SMALL_SPEED = 7.1884;

        private static final double MEDIUM_TOTAL_ANGLE = 110;
        private static final double SMALL_TOTAL_ANGLE = 360 - MEDIUM_TOTAL_ANGLE;

        private final int ringMod;
        private final int mediumSymmetry;
        private final int smallSymmetry;

        private final int largeSpawnTick;
        private final int largeMod;

        private final double largeSpeedLow;
        private final double largeSpeedHigh;
        private final int largeSymmetryLow;
        private final int largeSymmetryHigh;

        public Template(int ringMod,
                        int mediumSymmetry,
                        int smallSymmetry,
                        int largeSpawnTick,
                        int largeMod,
                        double largeSpeedLow,
                        double largeSpeedHigh,
                        int largeSymmetryLow,
                        int largeSymmetryHigh,
                        SpawnBuilder spawnBuilder) {
            super(B3_PATTERN_2_1, spawnBuilder, componentTypeContainer);
            this.ringMod = ringMod;
            this.mediumSymmetry = mediumSymmetry;
            this.smallSymmetry = smallSymmetry;
            this.largeSpawnTick = largeSpawnTick;
            this.largeMod = largeMod;
            this.largeSpeedLow = largeSpeedLow;
            this.largeSpeedHigh = largeSpeedHigh;
            this.largeSymmetryLow = largeSymmetryLow;
            this.largeSymmetryHigh = largeSymmetryHigh;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, ringMod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double baseAngle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
                DoublePoint basePos = new PolarVector(15, baseAngle).add(pos);
                SpawnUtil.arcFormation(
                        pos,
                        basePos,
                        new PolarVector(MEDIUM_SPEED, baseAngle),
                        mediumSymmetry,
                        MEDIUM_TOTAL_ANGLE,
                        (p, v) -> spawnMediumBullet(
                                p,
                                v,
                                sliceBoard
                        )
                );
                SpawnUtil.arcFormation(
                        pos,
                        basePos,
                        new PolarVector(SMALL_SPEED, baseAngle + 180),
                        smallSymmetry,
                        SMALL_TOTAL_ANGLE,
                        (p, v) -> spawnSmallBullet(
                                p,
                                v,
                                sliceBoard
                        )
                );
            }
            if (tick <= largeSpawnTick && tickMod(tick, largeMod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random random = GameUtil.getRandom(globalBoard);
                double angle = random.nextDouble() * 360;
                DoublePoint basePos = new PolarVector(15, angle).add(pos);
                double speed = RandomUtil.randDoubleInclusive(largeSpeedLow, largeSpeedHigh, random);

                int symmetry = RandomUtil.randIntInclusive(largeSymmetryLow, largeSymmetryHigh, random);

                AbstractVector baseVelocity = new PolarVector(speed, angle);

                SpawnUtil.ringFormation(pos, basePos, baseVelocity, symmetry, (p, v) -> spawnLargeBullet(p, v, sliceBoard));
            }
        }

        private void spawnSmallBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, BLUE, NORMAL_OUTBOUND, 10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

        private void spawnMediumBullet(DoublePoint pos,
                                       AbstractVector velocity,
                                       AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, BLUE, NORMAL_OUTBOUND, 0)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

        private void spawnLargeBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, LARGE, CHARTREUSE, LARGE_OUTBOUND, -10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}