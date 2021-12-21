package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
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
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B2_PATTERN_6_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B2_Pattern_6_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B2_Pattern_6_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(40,
                3,
                40,
                8,
                1,
                2,
                3,
                15,
                5,
                5.5,
                18,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(40,
                3,
                40,
                8,
                1,
                2,
                3,
                15,
                5,
                5.5,
                18,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(40,
                3,
                40,
                8,
                1,
                2,
                3,
                15,
                5,
                5.5,
                18,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(70,
                3,
                40,
                16,
                2,
                2,
                4,
                10,
                9,
                6,
                12,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final double RING_OFFSET_MAGNITUDE = 2;

        private static final double RING_INIT_SPEED = 3.7;
        private static final int RING_SLOW_TIME = 40;
        private static final int RING_WAIT_TIME_LOW = 20;
        private static final int RING_WAIT_TIME_HIGH = 50;
        private static final int RING_WAIT_TIME_RANGE = RING_WAIT_TIME_HIGH - RING_WAIT_TIME_LOW;
        private static final int RING_SPEED_TIME = 40;

        private static final double RING_FINAL_ANGLE_MULTIPLIER_MIN = 1.5;
        private static final double RING_FINAL_ANGLE_MULTIPLIER_MAX = 7;

        private final int ringSymmetry;
        private final double ringFinalSpeed;

        private final int fastRingMod;
        private final int fastRingSymmetry;
        private final int fastRingRows;
        private final int fastRingSpeedLow;
        private final int fastRingSpeedHigh;

        private final int shotMod;
        private final int shotSymmetry;
        private final double shotSpeed;
        private final double shotAngleIncrement;

        private Template(int ringSymmetry,
                         double ringFinalSpeed,
                         int fastRingMod,
                         int fastRingSymmetry,
                         int fastRingRows,
                         int fastRingSpeedLow,
                         int fastRingSpeedHigh,
                         int shotMod,
                         int shotSymmetry,
                         double shotSpeed,
                         double shotAngleIncrement,
                         SpawnBuilder spawnBuilder) {
            super(B2_PATTERN_6_1, spawnBuilder, componentTypeContainer);
            this.ringSymmetry = ringSymmetry;
            this.ringFinalSpeed = ringFinalSpeed;
            this.fastRingMod = fastRingMod;
            this.fastRingSymmetry = fastRingSymmetry;
            this.fastRingRows = fastRingRows;
            this.fastRingSpeedLow = fastRingSpeedLow;
            this.fastRingSpeedHigh = fastRingSpeedHigh;
            this.shotMod = shotMod;
            this.shotSymmetry = shotSymmetry;
            this.shotSpeed = shotSpeed;
            this.shotAngleIncrement = shotAngleIncrement;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, 50)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random random = GameUtil.getRandom(ecsInterface.getGlobalBoard());

                if (tick == 500 || tick == 450) {
                    spawnRing(sliceBoard, random, pos, RED);
                } else if (tick == 400 || tick == 350) {
                    spawnRing(sliceBoard, random, pos, CYAN);
                } else if (tick == 300) {
                    spawnOffsetRing(sliceBoard, random, pos, -30, BLUE);
                    spawnOffsetRing(sliceBoard, random, pos, 90, SPRING);
                    spawnOffsetRing(sliceBoard, random, pos, 210, ROSE);
                }
            }
            if (tick >= 50 && tickMod(tick, fastRingMod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                double angle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

                DoublePoint basePos = new ConstPolarVector(10, angle).add(pos);
                SpawnUtil.columnFormation(fastRingSpeedLow, fastRingSpeedHigh, fastRingRows, (speed) -> SpawnUtil.ringFormation(pos, basePos, new PolarVector(speed, angle), fastRingSymmetry, (p, v) -> spawnFastRingBullet(p, v, sliceBoard)));
            }
            if (tick <= 70 && tick >= 20 && tickMod(tick, shotMod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                double angle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

                DoublePoint basePos = new ConstPolarVector(10, angle).add(pos);
                SpawnUtil.arcFormationIncrement(pos, basePos, new PolarVector(shotSpeed, angle), shotSymmetry, shotAngleIncrement, (p, v) -> spawnShotBullet(p, v, sliceBoard));
            }
        }

        private void spawnRing(AbstractPublishSubscribeBoard sliceBoard,
                               Random random,
                               DoublePoint pos,
                               EnemyProjectileColors color) {

            double angle = RandomUtil.randDoubleInclusive(0, 360, random);
            DoublePoint basePos = new ConstPolarVector(10, angle).add(pos);
            AbstractVector baseVelocity = new PolarVector(RING_INIT_SPEED, angle);

            double ringFinalAngleMultiplier = RandomUtil.randDoubleInclusive(RING_FINAL_ANGLE_MULTIPLIER_MIN, RING_FINAL_ANGLE_MULTIPLIER_MAX, random);

            AtomicReference<Integer> counter = new AtomicReference<>(0); //what in the world is this
            SpawnUtil.ringFormation(pos, basePos, baseVelocity, ringSymmetry, (p, v) -> {
                double finalAngle = angle - v.getAngle().getAngle() * ringFinalAngleMultiplier;
                AbstractVector finalVelocity = new PolarVector(ringFinalSpeed, finalAngle);
                int waitTime = (int) (RING_WAIT_TIME_LOW + ((((double) counter.get()) / ringSymmetry) * RING_WAIT_TIME_RANGE));
                spawnRingBullet(p, v, finalVelocity, waitTime, color, -2, sliceBoard);
                counter.set(counter.get() + 1);
            });
        }

        private void spawnOffsetRing(AbstractPublishSubscribeBoard sliceBoard,
                                     Random random,
                                     DoublePoint pos,
                                     double ringOffsetAngle,
                                     EnemyProjectileColors color) {

            double angle = RandomUtil.randDoubleInclusive(0, 360, random);
            DoublePoint basePos = new ConstPolarVector(10, angle).add(pos);
            AbstractVector baseVelocity = new PolarVector(RING_INIT_SPEED, angle);

            double ringFinalAngleMultiplier = RandomUtil.randDoubleInclusive(RING_FINAL_ANGLE_MULTIPLIER_MIN, RING_FINAL_ANGLE_MULTIPLIER_MAX, random);

            AbstractVector offsetVector = new PolarVector(RING_OFFSET_MAGNITUDE, ringOffsetAngle);

            AtomicReference<Integer> counter = new AtomicReference<>(0); //what in the world is this
            SpawnUtil.ringFormation(pos, basePos, baseVelocity, ringSymmetry, (p, v) -> {
                double finalAngle = angle - v.getAngle().getAngle() * ringFinalAngleMultiplier;
                AbstractVector finalVelocity = new PolarVector(ringFinalSpeed, finalAngle);
                int waitTime = (int) (RING_WAIT_TIME_LOW + ((((double) counter.get()) / ringSymmetry) * RING_WAIT_TIME_RANGE));
                AbstractVector velocity = GeometryUtil.vectorAdd(v, offsetVector);
                spawnRingBullet(p, velocity, finalVelocity, waitTime, color, counter.get(), sliceBoard);
                counter.set(counter.get() + 1);
            });
        }

        private void spawnRingBullet(DoublePoint pos,
                                     AbstractVector velocity,
                                     AbstractVector finalVelocity,
                                     int waitTime,
                                     EnemyProjectileColors color,
                                     int relativeDrawOrder,
                                     AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, color, -100, relativeDrawOrder)
                            .setProgram(makeRingProgram(finalVelocity, waitTime))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeRingProgram(AbstractVector finalVelocity, int waitTime) {
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE
            ).linkInject(
                    ProgramBuilder.linearLink(
                            new InstructionNode<>(SLOW_TO_HALT, RING_SLOW_TIME),
                            new InstructionNode<>(TIMER, waitTime),
                            new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(finalVelocity, RING_SPEED_TIME))
                    )
            ).compile();
        }

        private void spawnFastRingBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, BLUE, NORMAL_OUTBOUND, ringSymmetry + 1)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

        private void spawnShotBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, LARGE, BLUE, LARGE_OUTBOUND, -10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}