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
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B6_PATTERN_3_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B6_Pattern_3_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B6_Pattern_3_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(15,
                80,
                15,
                140,
                2.8356,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(12,
                75,
                19,
                160,
                2.8356,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(12,
                60,
                25,
                180,
                2.8356,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(10,
                50,
                27,
                200,
                2.8356,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final double RING_SLOW_SPEED = 1.3;
        private static final double RING_FAST_SPEED = 3.2;

        private static final int RING_SPEED_DURATION = 30;

        private final int ringMod;
        private final int largeMod;

        private final int ringSymmetry;
        private final int ringWaitTime;

        private final double largeSpeed;

        private Template(int ringMod,
                         int largeMod,
                         int ringSymmetry,
                         int ringWaitTime,
                         double largeSpeed,
                         SpawnBuilder spawnBuilder) {
            super(B6_PATTERN_3_1, spawnBuilder, componentTypeContainer);
            this.ringMod = ringMod;
            this.largeMod = largeMod;
            this.ringSymmetry = ringSymmetry;
            this.ringWaitTime = ringWaitTime;
            this.largeSpeed = largeSpeed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, ringMod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = getRandomPoint(random);

                double angle = random.nextDouble() * 360;

                AbstractVector baseInitVelocity = new PolarVector(RING_SLOW_SPEED, angle);
                DoublePoint basePos = new PolarVector(10, angle).add(pos);

                SpawnUtil.ringFormation(pos, basePos, baseInitVelocity, ringSymmetry, (p, initVelocity) -> {
                    AbstractVector finalVelocity = new PolarVector(RING_FAST_SPEED, initVelocity.getAngle());
                    spawnRingBullet(p, initVelocity, finalVelocity, sliceBoard);
                });
            }
            if (tickMod(tick, largeMod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = getRandomPoint(random);

                double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

                DoublePoint spawnPos = new PolarVector(10, angleToPlayer).add(pos);
                AbstractVector velocity = new PolarVector(largeSpeed, angleToPlayer);

                spawnLargeBullet(spawnPos, velocity, sliceBoard);
            }
        }

        private DoublePoint getRandomPoint(Random random) {
            double x = RandomUtil.randDoubleInclusive(BOSS_BOUNDS.getXLow(), BOSS_BOUNDS.getXHigh(), random);
            double y = RandomUtil.randDoubleInclusive(BOSS_BOUNDS.getYLow(), BOSS_BOUNDS.getYHigh(), random);
            return new DoublePoint(x, y);
        }

        private void spawnRingBullet(DoublePoint pos,
                                     AbstractVector initVelocity,
                                     AbstractVector finalVelocity,
                                     AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, initVelocity, SHARP, YELLOW, NORMAL_OUTBOUND, 10)
                            .setProgram(makeRingProgram(finalVelocity))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeRingProgram(AbstractVector finalVelocity) {
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE,
                    new InstructionNode<>(TIMER, ringWaitTime),
                    new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(finalVelocity, RING_SPEED_DURATION))
            ).compile();
        }

        private void spawnLargeBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, LARGE, ORANGE, LARGE_OUTBOUND, -10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}