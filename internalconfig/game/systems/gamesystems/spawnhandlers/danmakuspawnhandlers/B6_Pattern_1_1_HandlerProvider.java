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
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B6_PATTERN_1_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static util.math.Constants.*;

public class B6_Pattern_1_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B6_Pattern_1_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(2,
                80,
                2,
                20.5,
                360/(PHI * 2),
                140,
                8,
                2.8356,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(2,
                80,
                3,
                20.5,
                360/PHI,
                160,
                10,
                2.8356,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(1,
                80,
                3,
                20.5,
                360/PHI,
                180,
                12,
                2.8356,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(1,
                80,
                4,
                20.5,
                360/PHI,
                200,
                14,
                2.8356,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int SPIRAL_FORMATION_MAX_TICK = 413;

        private static final double ARC_SLOW_SPEED = 1.3;
        private static final double ARC_FAST_SPEED = 3.2;

        private static final int ARC_SPEED_DURATION = 30;

        private final int arcMod;
        private final int largeMod;

        private final int arcSymmetry;
        private final double arcTotalAngle;
        private final double arcAngularVelocity;
        private final int arcWaitTime;

        private final int largeSymmetry;
        private final double largeSpeed;

        private Template(int arcMod,
                         int largeMod,
                         int arcSymmetry,
                         double arcTotalAngle,
                         double arcAngularVelocity,
                         int arcWaitTime,
                         int largeSymmetry,
                         double largeSpeed,
                         SpawnBuilder spawnBuilder) {
            super(B6_PATTERN_1_1, spawnBuilder, componentTypeContainer);
            this.arcMod = arcMod;
            this.largeMod = largeMod;
            this.arcSymmetry = arcSymmetry;
            this.arcTotalAngle = arcTotalAngle;
            this.arcAngularVelocity = arcAngularVelocity;
            this.arcWaitTime = arcWaitTime;
            this.largeSymmetry = largeSymmetry;
            this.largeSpeed = largeSpeed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, arcMod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double baseAngle = pseudoRandom.nextDouble() * 360;

                SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle, arcAngularVelocity, (angle) -> {
                    AbstractVector baseInitVelocity = new PolarVector(ARC_SLOW_SPEED, angle);
                    DoublePoint basePos = new PolarVector(10, angle).add(pos);
                    SpawnUtil.arcFormation(pos, basePos, baseInitVelocity, arcSymmetry, arcTotalAngle, (p, initVelocity) -> {
                        AbstractVector finalVelocity = new PolarVector(ARC_FAST_SPEED, initVelocity.getAngle());
                        spawnArcBullet(p, initVelocity, finalVelocity, sliceBoard);
                    });
                });
            }
            if (tickMod(tick, largeMod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double baseAngle = random.nextDouble() * 360;

                DoublePoint basePos = new PolarVector(10, baseAngle).add(pos);
                AbstractVector baseVelocity = new PolarVector(largeSpeed, baseAngle);

                SpawnUtil.ringFormation(pos, basePos, baseVelocity, largeSymmetry, (p, v) -> spawnLargeBullet(p, v, sliceBoard));
            }
        }

        private void spawnArcBullet(DoublePoint pos,
                                    AbstractVector initVelocity,
                                    AbstractVector finalVelocity,
                                    AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, initVelocity, SHARP, YELLOW, NORMAL_OUTBOUND, 10)
                            .setProgram(makeArcProgram(finalVelocity))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeArcProgram(AbstractVector finalVelocity) {
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE,
                    new InstructionNode<>(TIMER, arcWaitTime),
                    new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(finalVelocity, ARC_SPEED_DURATION))
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