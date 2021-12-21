package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B3_PATTERN_4_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B3_Pattern_4_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B3_Pattern_4_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                5.5,
                3,
                143,
                3,
                2,
                60,
                1.837,
                4,
                5,
                1.5,
                8,
                .1,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                5.5,
                3,
                143,
                3,
                3,
                60,
                1.837,
                4,
                15,
                1.2,
                10,
                .1,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                5.5,
                4,
                143,
                4,
                3,
                60,
                1.837,
                4,
                20,
                1,
                10,
                .1,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                5.5,
                5,
                143,
                4,
                3,
                60,
                1.837,
                4,
                25,
                .8,
                10,
                .1,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int ARC_LENGTH = 5 * 30;
        private final int ARC_END = B3_PATTERN_4_1.getDuration() - ARC_LENGTH; //cannot make static

        private static final int COLUMN_START = 90;

        private static final int ARC_MOD = ARC_LENGTH / 5;

        private static final int ARC_SLOW_DURATION = 55;
        private static final int ARC_SPEED_DURATION = 55;
        private static final double ARC_FINAL_SPEED = 3.483;

        private static final int COLUMN_MOD = 30;
        private static final double COLUMN_INIT_SPEED = .2;

        private final double arcSpeed;
        private final int arcSymmetry;
        private final double arcTotalAngle;

        private final int arcStraightSymmetry;
        private final int arcStraightRows;
        private final double arcStraightTotalAngle;
        private final double arcStraightSpeedLow;
        private final double arcStraightSpeedHigh;

        private final int columnRows;
        private final double columnSpeedLow;
        private final double columnSpeedHigh;
        private final double columnAcceleration;

        private Template(double arcSpeed,
                         int arcSymmetry,
                         double arcTotalAngle,
                         int arcStraightSymmetry,
                         int arcStraightRows,
                         double arcStraightTotalAngle,
                         double arcStraightSpeedLow,
                         double arcStraightSpeedHigh,
                         int columnRows,
                         double columnSpeedLow,
                         double columnSpeedHigh,
                         double columnAcceleration,
                         SpawnBuilder spawnBuilder) {
            super(B3_PATTERN_4_1, spawnBuilder, componentTypeContainer);
            this.arcSpeed = arcSpeed;
            this.arcSymmetry = arcSymmetry;
            this.arcTotalAngle = arcTotalAngle;
            this.arcStraightSymmetry = arcStraightSymmetry;
            this.arcStraightRows = arcStraightRows;
            this.arcStraightTotalAngle = arcStraightTotalAngle;
            this.arcStraightSpeedLow = arcStraightSpeedLow;
            this.arcStraightSpeedHigh = arcStraightSpeedHigh;
            this.columnRows = columnRows;
            this.columnSpeedLow = columnSpeedLow;
            this.columnSpeedHigh = columnSpeedHigh;
            this.columnAcceleration = columnAcceleration;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tick >= ARC_END && tickMod(tick, ARC_MOD)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double baseAngle = RandomUtil.randDoubleInclusive(0, 360, random);

                SpawnUtil.ringFormation(pos, new PolarVector(arcSpeed, baseAngle + 120), 3, (ringPos, ringVel) -> {
                    Angle ringAngle = ringVel.getAngle();
                    AbstractVector finalVelocity = new PolarVector(ARC_FINAL_SPEED, ringAngle.add(180));
                    DoublePoint basePos = new PolarVector(15, ringAngle).add(pos);
                    SpawnUtil.arcFormation(
                            pos,
                            basePos,
                            ringVel,
                            arcSymmetry,
                            arcTotalAngle,
                            (p, v) -> spawnLargeArcBullet(
                                    p,
                                    v,
                                    finalVelocity,
                                    sliceBoard
                            )
                    );
                    Angle straightAngle = ringAngle.add(60);
                    DoublePoint straightBasePos = new PolarVector(15, straightAngle).add(pos);
                    SpawnUtil.columnFormation(arcStraightSpeedLow, arcStraightSpeedHigh, arcStraightRows, (speed) -> SpawnUtil.arcFormation(
                            pos,
                            straightBasePos,
                            new PolarVector(speed, straightAngle),
                            arcStraightSymmetry,
                            arcStraightTotalAngle,
                            (p, v) -> spawnMediumBullet(p, v, sliceBoard)
                    ));
                });
            } else if (tick <= COLUMN_START && tickMod(tick, COLUMN_MOD)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double angle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
                DoublePoint basePos = new PolarVector(10, angle).add(pos);

                AbstractVector baseVelocity = new PolarVector(COLUMN_INIT_SPEED, angle);

                SpawnUtil.columnFormation(columnSpeedLow, columnSpeedHigh, columnRows, (finalSpeed) -> SpawnUtil.ringFormation(pos, basePos, baseVelocity, 3, (p, ringVelocity) -> spawnSmallBullet(p, ringVelocity, finalSpeed, sliceBoard)));
            }
        }

        private void spawnLargeArcBullet(DoublePoint pos,
                                         AbstractVector velocity,
                                         AbstractVector finalVelocity,
                                         AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, LARGE, RED, -200, -10)
                            .setProgram(makeLargeArcProgram(finalVelocity))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeLargeArcProgram(AbstractVector finalVelocity) {
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE
            ).linkInject(
                    ProgramBuilder.linearLink(
                            new InstructionNode<>(SLOW_TO_HALT, ARC_SLOW_DURATION),
                            new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(finalVelocity, ARC_SPEED_DURATION))
                    )
            ).compile();
        }

        private void spawnMediumBullet(DoublePoint pos,
                                       AbstractVector velocity,
                                       AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, ROSE, NORMAL_OUTBOUND, 0)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

        private void spawnSmallBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      double finalSpeed,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(
                            pos,
                            velocity,
                            SMALL,
                            ROSE,
                            NORMAL_OUTBOUND,
                            2
                    )
                            .setProgram(ProgramUtil.makeAcceleratingBulletProgram(finalSpeed, columnAcceleration).compile())
                            .packageAsMessage()
            );
        }
    }
}