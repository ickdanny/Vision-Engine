package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B6_PATTERN_9_2;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B6_Pattern_9_2_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B6_Pattern_9_2_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                60,
                1,
                1.4,
                2.3,
                5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                77,
                2,
                .8,
                2.5,
                7,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                84,
                2,
                .8,
                2.5,
                7,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                91,
                3,
                .8,
                2.5,
                7,
                spawnBuilder
        );
    }

    private class Template extends AbstractPositionSpawnHandler {

        private static final int WAIT_TIME = 48;
        private static final int SPEED_DURATION = 37;

        private static final double SPEED_CONSTANT = 1.5;

        private final int symmetry;
        private final int rows;
        private final double speedLow;
        private final double speedHigh;
        private final int spikeWidth;

        private Template(int symmetry,
                         int rows,
                         double speedLow,
                         double speedHigh,
                         int spikeWidth,
                         SpawnBuilder spawnBuilder) {
            super(B6_PATTERN_9_2, spawnBuilder, componentTypeContainer);
            if (symmetry % spikeWidth != 0) {
                throw new RuntimeException("spikeWidth does not divide symmetry evenly: " + spikeWidth + "/" + symmetry);
            }
            this.symmetry = symmetry;
            this.rows = rows;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.spikeWidth = spikeWidth;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            Random random = GameUtil.getRandom(globalBoard);

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            double baseAngle = random.nextDouble() * 360;
            DoublePoint basePos = new PolarVector(20, baseAngle).add(pos);

            SpawnUtil.columnFormation(speedLow, speedHigh, rows, (baseInitSpeed) -> {
                AbstractVector baseInitVelocity = new PolarVector(baseInitSpeed, baseAngle);

                AtomicInteger atomicInteger = new AtomicInteger(0);
                AtomicBoolean isAtomicIntegerIncreasing = new AtomicBoolean(true);

                SpawnUtil.ringFormation(pos, basePos, baseInitVelocity, symmetry, (p, initVelocity) -> {
                    double speedRatio = 1 + ((SPEED_CONSTANT * atomicInteger.get()) / ((double) spikeWidth));
                    double finalSpeed = speedRatio * baseInitSpeed;
                    AbstractVector finalVelocity = new PolarVector(finalSpeed, initVelocity.getAngle());
                    spawnBullet(p, initVelocity, finalVelocity, sliceBoard);

                    if (isAtomicIntegerIncreasing.get()) {
                        int nextAtomicIntegerValue = atomicInteger.get() + 1;
                        if (nextAtomicIntegerValue >= (spikeWidth / 2) + 1) {
                            nextAtomicIntegerValue -= 2;
                            isAtomicIntegerIncreasing.set(false);
                        }
                        atomicInteger.set(nextAtomicIntegerValue);
                    } else {
                        int nextAtomicIntegerValue = atomicInteger.get() - 1;
                        if (nextAtomicIntegerValue < 0) {
                            nextAtomicIntegerValue += 2;
                            isAtomicIntegerIncreasing.set(true);
                        }
                        atomicInteger.set(nextAtomicIntegerValue);
                    }
                });
            });
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector initVelocity,
                                 AbstractVector finalVelocity,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, initVelocity, SHARP, ORANGE, NORMAL_OUTBOUND, 10)
                            .setProgram(makeProgram(finalVelocity))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeProgram(AbstractVector finalVelocity) {
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE,
                    new InstructionNode<>(TIMER, WAIT_TIME),
                    new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(finalVelocity, SPEED_DURATION))
            ).compile();
        }
    }
}