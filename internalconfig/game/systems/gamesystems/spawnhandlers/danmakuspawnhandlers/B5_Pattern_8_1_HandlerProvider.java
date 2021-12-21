package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B5_PATTERN_8_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B5_Pattern_8_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B5_Pattern_8_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(20,
                -.6,
                3,
                3,
                4.253,

                5,
                3,
                5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(20,
                -.6,
                3,
                4,
                4.253,

                7,
                3,
                5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(20,
                -.6,
                3,
                4,
                4.253,

                9,
                3,
                5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(20,
                -.6,
                3,
                5,
                4.253,

                11,
                3,
                5,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int SPIRAL_FORMATION_MAX_TICK = 413;

        private static final double SPAWN_DIST = 50;
        private static final double TOTAL_WIDTH = 100;

        private static final double BARRIER_SPEED = 1.8942;

        private static final int SHARP_WAIT_TIME = 40;
        private static final int SHARP_TURN_TIME = 27;

        private final int mod;
        private final double angularVelocity;

        private final int barrierSpawns;

        private final int mediumSpawns;
        private final double mediumSpeed;

        private final int sharpSpawns;
        private final double sharpSpeed;
        private final double sharpTurnRange;

        private Template(int mod,
                         double angularVelocity,
                         int barrierSpawns,
                         int mediumSpawns,
                         double mediumSpeed,
                         int sharpSpawns,
                         double sharpSpeed,
                         double sharpTurnRange,
                         SpawnBuilder spawnBuilder) {
            super(B5_PATTERN_8_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.angularVelocity = angularVelocity;
            this.barrierSpawns = barrierSpawns;
            this.mediumSpawns = mediumSpawns;
            this.mediumSpeed = mediumSpeed;
            this.sharpSpawns = sharpSpawns;
            this.sharpSpeed = sharpSpeed;
            this.sharpTurnRange = sharpTurnRange;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random random = GameUtil.getRandom(globalBoard);
                Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);

                double baseAngle = pseudoRandom.nextDouble() * 360;

                SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle, angularVelocity, (spiralAngle) -> {
                    DoublePoint baseSpawnPos = new PolarVector(SPAWN_DIST, spiralAngle).add(pos);
                    SpawnUtil.ringFormation(pos, baseSpawnPos, 2, (spawnPos) -> {
                        Angle angle = GeometryUtil.angleFromAToB(pos, spawnPos);
                        Angle blockAngle = angle.add(90);
                        SpawnUtil.fullBlockFormation(spawnPos, blockAngle, TOTAL_WIDTH, barrierSpawns, (p) -> {
                            AbstractVector velocity = new PolarVector(BARRIER_SPEED, angle);
                            spawnBarrier(p, velocity, sliceBoard);
                        });
                    });
                    Angle mediumAngle = spiralAngle.add(90);
                    baseSpawnPos = new PolarVector(SPAWN_DIST, mediumAngle).add(pos);
                    SpawnUtil.ringFormation(pos, baseSpawnPos, 2, (spawnPos) -> {
                        Angle angle = GeometryUtil.angleFromAToB(pos, spawnPos);
                        Angle blockAngle = angle.add(90);
                        SpawnUtil.fullBlockFormation(spawnPos, blockAngle, TOTAL_WIDTH, mediumSpawns, (p) -> {
                            AbstractVector velocity = new PolarVector(mediumSpeed, angle);
                            spawnMediumBullet(p, velocity, sliceBoard);
                        });
                    });

                    Angle redAngle = spiralAngle.add(45);
                    baseSpawnPos = new PolarVector(SPAWN_DIST, redAngle).add(pos);
                    SpawnUtil.ringFormation(pos, baseSpawnPos, 2, (spawnPos) -> {
                        Angle blockAngle = GeometryUtil.angleFromAToB(pos, spawnPos).add(90);
                        SpawnUtil.fullBlockFormation(spawnPos, blockAngle, TOTAL_WIDTH, sharpSpawns, (p) -> {
                            AbstractVector velocity = GeometryUtil.vectorFromAToB(pos, p);
                            velocity.scale(sharpSpeed/(Math.sqrt(2) * SPAWN_DIST));//speed = diagonal speed
                            DoublePoint basePos = new PolarVector(10, velocity.getAngle()).add(pos);
                            Angle finalAngle = velocity.getAngle().add(RandomUtil.randDoubleInclusive(-sharpTurnRange, sharpTurnRange, random));
                            spawnSharpBullet(basePos, velocity, finalAngle, RED, sliceBoard);
                        });
                    });

                    Angle violetAngle = redAngle.add(90);
                    baseSpawnPos = new PolarVector(SPAWN_DIST, violetAngle).add(pos);
                    SpawnUtil.ringFormation(pos, baseSpawnPos, 2, (spawnPos) -> {
                        Angle blockAngle = GeometryUtil.angleFromAToB(pos, spawnPos).add(90);
                        SpawnUtil.fullBlockFormation(spawnPos, blockAngle, TOTAL_WIDTH, sharpSpawns, (p) -> {
                            AbstractVector velocity = GeometryUtil.vectorFromAToB(pos, p);
                            velocity.scale(sharpSpeed/(Math.sqrt(2) * SPAWN_DIST));//speed = diagonal speed
                            DoublePoint basePos = new PolarVector(10, velocity.getAngle()).add(pos);
                            Angle finalAngle = velocity.getAngle().add(RandomUtil.randDoubleInclusive(-sharpTurnRange, sharpTurnRange, random));
                            spawnSharpBullet(basePos, velocity, finalAngle, VIOLET, sliceBoard);
                        });
                    });
                });
            }
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

        private void spawnSharpBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      Angle finalAngle,
                                      EnemyProjectileColors color,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, color, NORMAL_OUTBOUND, 10)
                            .setProgram(ProgramUtil.makeShortTurnBulletProgram(SHARP_WAIT_TIME, finalAngle, SHARP_TURN_TIME).compile())
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