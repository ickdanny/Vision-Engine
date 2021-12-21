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
import static internalconfig.game.components.spawns.DanmakuSpawns.B3_PATTERN_1_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B3_Pattern_1_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B3_Pattern_1_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    //MAKE SURE ARC SYMMETRY IS ODD - OTHERWISE GAP

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                7,
                8,
                210,
                5,
                8,
                1.5,
                5,
                8,
                16,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                7,
                9,
                210,
                7,
                7,
                1.5,
                5,
                8,
                16,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                7,
                11,
                210,
                9,
                6,
                1.5,
                5,
                8,
                16,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                7,
                13,
                210,
                9,
                5,
                1.5,
                5,
                8,
                16,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int ARC_LENGTH = 3 * 50;
        private final int ARC_END = B3_PATTERN_1_1.getDuration() - ARC_LENGTH; //cannot make static

        private static final int RING_TIME_BUFFER = 20;

        private static final int ARC_MOD = ARC_LENGTH / 3;

        private static final int ARC_SLOW_DURATION = 55;
        private static final int ARC_SPEED_DURATION = 55;
        private static final double ARC_FINAL_SPEED = 3.483;

        private final double arcSpeed;
        private final int arcSymmetry;
        private final double arcTotalAngle;

        private final int arcStraightSymmetry;

        private final int ringMod;
        private final double ringSpeedLow;
        private final double ringSpeedHigh;
        private final int ringSymmetryLow;
        private final int ringSymmetryHigh;

        private Template(double arcSpeed,
                         int arcSymmetry,
                         double arcTotalAngle,
                         int arcStraightSymmetry,
                         int ringMod,
                         double ringSpeedLow,
                         double ringSpeedHigh,
                         int ringSymmetryLow,
                         int ringSymmetryHigh,
                         SpawnBuilder spawnBuilder) {
            super(B3_PATTERN_1_1, spawnBuilder, componentTypeContainer);
            this.arcSpeed = arcSpeed;
            this.arcSymmetry = arcSymmetry;
            this.arcTotalAngle = arcTotalAngle;
            this.arcStraightSymmetry = arcStraightSymmetry;
            this.ringMod = ringMod;
            this.ringSpeedLow = ringSpeedLow;
            this.ringSpeedHigh = ringSpeedHigh;
            this.ringSymmetryLow = ringSymmetryLow;
            this.ringSymmetryHigh = ringSymmetryHigh;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tick >= ARC_END && tickMod(tick, ARC_MOD)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double baseAngle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
                DoublePoint basePos = new PolarVector(15, baseAngle).add(pos);
                AbstractVector finalVelocity = new PolarVector(ARC_FINAL_SPEED, baseAngle);
                SpawnUtil.arcFormation(
                        pos,
                        basePos,
                        new PolarVector(arcSpeed, baseAngle + 180),
                        arcSymmetry,
                        arcTotalAngle,
                        (p, v) -> spawnMediumArcBullet(
                                p,
                                v,
                                finalVelocity,
                                sliceBoard
                        )
                );
                SpawnUtil.arcFormation(
                        pos,
                        basePos,
                        new PolarVector(ARC_FINAL_SPEED, baseAngle),
                        arcStraightSymmetry,
                        360 - arcTotalAngle,
                        (p, v) -> spawnMediumStraightBullet(
                                p,
                                v,
                                sliceBoard
                        )
                );

            } else if (tick < ARC_END - RING_TIME_BUFFER && tickMod(tick, ringMod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random random = GameUtil.getRandom(globalBoard);
                double angle = random.nextDouble() * 360;
                DoublePoint basePos = new PolarVector(15, angle).add(pos);
                double speed = RandomUtil.randDoubleInclusive(ringSpeedLow, ringSpeedHigh, random);

                int symmetry = RandomUtil.randIntInclusive(ringSymmetryLow, ringSymmetryHigh, random);

                AbstractVector baseVelocity = new PolarVector(speed, angle);

                SpawnUtil.ringFormation(pos, basePos, baseVelocity, symmetry, (p, v) -> spawnSmallBullet(p, v, sliceBoard));
            }
        }

        private void spawnMediumArcBullet(DoublePoint pos,
                                          AbstractVector velocity,
                                          AbstractVector finalVelocity,
                                          AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, BLUE, -200, -2)
                            .setProgram(makeMediumArcProgram(finalVelocity))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeMediumArcProgram(AbstractVector finalVelocity) {
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

        private void spawnMediumStraightBullet(DoublePoint pos,
                                               AbstractVector velocity,
                                               AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, CYAN, NORMAL_OUTBOUND, 10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

        private void spawnSmallBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, CYAN, NORMAL_OUTBOUND, 10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}
