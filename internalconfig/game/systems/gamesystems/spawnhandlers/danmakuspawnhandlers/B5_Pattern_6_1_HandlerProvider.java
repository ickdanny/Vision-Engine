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
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B5_PATTERN_6_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B5_Pattern_6_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B5_Pattern_6_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(20,
                .6 * 2,
                3,
                1.7,
                4.5,
                30,
                .6,
                7,
                2.5252,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(17,
                .6 * 2,
                6,
                1.7,
                4.5,
                27,
                .6,
                10,
                2.5252,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(13,
                .6 * 2,
                6,
                1.7,
                4.5,
                23,
                .6,
                10,
                2.5252,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(11,
                .6 * 2,
                6,
                1.7,
                4.5,
                21,
                .6,
                10,
                2.5252,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int SPIRAL_FORMATION_MAX_TICK = 413;

        private static final double SQUARE_OUT_DIST = 100;
        private static final double SQUARE_TOTAL_WIDTH = 200;

        private static final int BARRIER_FREQUENCY = 5; //1 in BARRIER_FREQUENCY squares are barriers

        private final int sharpMod;

        private final double sharpAngularVelocity;

        private final int sharpRows;
        private final double sharpSpeedLow;
        private final double sharpSpeedHigh;

        private final int squareMod;
        private final double squareAngularVelocity;
        private final int squareSpawns;
        private final double squareSpeed;

        private Template(int sharpMod,
                         double sharpAngularVelocity,
                         int sharpRows,
                         double sharpSpeedLow,
                         double sharpSpeedHigh,
                         int squareMod,
                         double squareAngularVelocity,
                         int squareSpawns,
                         double squareSpeed,
                         SpawnBuilder spawnBuilder) {
            super(B5_PATTERN_6_1, spawnBuilder, componentTypeContainer);
            this.sharpMod = sharpMod;
            this.sharpAngularVelocity = sharpAngularVelocity;
            this.sharpRows = sharpRows;
            this.sharpSpeedLow = sharpSpeedLow;
            this.sharpSpeedHigh = sharpSpeedHigh;
            this.squareMod = squareMod;
            this.squareAngularVelocity = squareAngularVelocity;
            this.squareSpawns = squareSpawns;
            this.squareSpeed = squareSpeed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, sharpMod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);

                double baseAngle = pseudoRandom.nextDouble() * 360;

                SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle, sharpAngularVelocity, (angle) -> SpawnUtil.columnFormation(sharpSpeedLow, sharpSpeedHigh, sharpRows, (speed) -> {
                    AbstractVector baseVelocity = new PolarVector(speed, angle);
                    DoublePoint basePos = new PolarVector(10, angle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, baseVelocity, 4, (p, v) -> spawnSharpBullet(p, v, sliceBoard));
                }));
            }
            if (tickMod(tick, squareMod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);
                pseudoRandom.nextDouble();
                double baseAngle = (pseudoRandom.nextDouble() * 360) - 59;

                SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle, squareAngularVelocity, (angle) -> {
                    DoublePoint baseSpawnPos = new PolarVector(SQUARE_OUT_DIST, angle).add(pos);
                    SpawnUtil.ringFormation(pos, baseSpawnPos, 4, (spawnPos) -> {
                        Angle blockAngle = GeometryUtil.angleFromAToB(pos, spawnPos).add(90);
                        SpawnUtil.fullBlockFormation(spawnPos, blockAngle, SQUARE_TOTAL_WIDTH, squareSpawns, (p) -> {
                            AbstractVector velocity = GeometryUtil.vectorFromAToB(pos, p);
                            velocity.scale(squareSpeed/(Math.sqrt(2) * SQUARE_OUT_DIST));//speed = diagonal speed
                            DoublePoint basePos = new PolarVector(10, velocity.getAngle()).add(pos);
                            if(tickMod(tick, squareMod * BARRIER_FREQUENCY)){
                                spawnBarrier(basePos, velocity, sliceBoard);
                            }else{
                                spawnSquareBullet(basePos, velocity, sliceBoard);
                            }
                        });
                    });
                });
            }
        }

        private void spawnSharpBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, ORANGE, NORMAL_OUTBOUND, 10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

        private void spawnSquareBullet(DoublePoint pos,
                                       AbstractVector velocity,
                                       AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, ROSE, NORMAL_OUTBOUND, 0)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

        private void spawnBarrier(DoublePoint pos,
                                  AbstractVector velocity,
                                  AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeBarrier(pos, velocity, NORMAL_OUTBOUND, 1)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}