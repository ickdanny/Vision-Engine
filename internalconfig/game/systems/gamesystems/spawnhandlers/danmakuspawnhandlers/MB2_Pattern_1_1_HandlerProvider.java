package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.components.spawns.DanmakuSpawns.MB2_PATTERN_1_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class MB2_Pattern_1_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public MB2_Pattern_1_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                           AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                11,
                3,
                3,
                3,
                -5,
                2.5,
                2.4,

                20,
                6,
                2,
                1,
                5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                10,
                4,
                4,
                3,
                -5,
                2.5,
                2.4,

                27,
                6,
                2,
                1,
                5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                9,
                4,
                4,
                3,
                -5,
                2.5,
                2.4,

                34,
                6,
                2,
                1,
                5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                8,
                4,
                4,
                3,
                -5,
                2.5,
                2.4,

                40,
                6,
                2,
                1,
                4.5,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private final static int SPIRAL_FORMATION_MAX_TICK = 273;

        private final static int AIMED_LOOP_DURATION = 120;

        private final static double SPIRAL_SPAWN_DISTANCE = 60;
        private final static int SPIRAL_PRE_SLOW_TIME = 10;
        private final static int SPIRAL_SLOW_DURATION = 70;
        private final static int SPIRAL_POST_SLOW_TIME = 1;

        private final static int AIMED_PRE_SLOW_TIME = 5;
        private final static int AIMED_SLOW_DURATION = 30;
        private final static int AIMED_POST_SLOW_TIME = 5;

        private final int spiralMod;
        private final int spiralSpawnSymmetry;
        private final int spiralSymmetry;
        private final double spiralSpawnAngularVelocity;
        private final double spiralAngularVelocity;
        private final double spiralSpeed;
        private final double spiralTurnAngularVelocity;

        private final int aimedLoopActiveDuration;
        private final int aimedMod;
        private final int aimedSymmetry;
        private final double aimedAngularVelocity;
        private final double aimedSpeed;

        private Template(int spiralMod,
                         int spiralSpawnSymmetry,
                         int spiralSymmetry,
                         double spiralSpawnAngularVelocity,
                         double spiralAngularVelocity,
                         double spiralSpeed,
                         double spiralTurnAngularVelocity,

                         int aimedLoopActiveDuration,
                         int aimedMod,
                         int aimedSymmetry,
                         int aimedAngularVelocity,
                         double aimedSpeed,

                         SpawnBuilder spawnBuilder) {
            super(MB2_PATTERN_1_1, spawnBuilder, componentTypeContainer);
            this.spiralMod = spiralMod;
            this.spiralSpawnSymmetry = spiralSpawnSymmetry;
            this.spiralSymmetry = spiralSymmetry;
            this.spiralSpawnAngularVelocity = spiralSpawnAngularVelocity;
            this.spiralAngularVelocity = spiralAngularVelocity;
            this.spiralSpeed = spiralSpeed;
            this.spiralTurnAngularVelocity = spiralTurnAngularVelocity;

            this.aimedLoopActiveDuration = aimedLoopActiveDuration;
            this.aimedMod = aimedMod;
            this.aimedSymmetry = aimedSymmetry;
            this.aimedAngularVelocity = aimedAngularVelocity;
            this.aimedSpeed = aimedSpeed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            boolean spawnSpiral = tickMod(tick, spiralMod);
            int aimedTick = (tick - 1) % AIMED_LOOP_DURATION;
            boolean spawnAimed = aimedTick < aimedLoopActiveDuration && tickMod(aimedTick, aimedMod);

            if(spawnSpiral || spawnAimed) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                if(spawnSpiral){
                    Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);
                    double spiralSpawnBaseAngle = pseudoRandom.nextDouble() * 360 - 124;
                    double baseAngle = pseudoRandom.nextDouble() * 360 + 32;
                    double baseTurnAngle = pseudoRandom.nextDouble() * 360;

                    SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, spiralSpawnBaseAngle, spiralSpawnAngularVelocity, (spiralSpawnAngle) -> SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle, spiralAngularVelocity, (initAngle) -> {

                        DoublePoint spiralSpawnBasePos = new PolarVector(SPIRAL_SPAWN_DISTANCE, spiralSpawnAngle).add(pos);

                        SpawnUtil.ringFormation(pos, spiralSpawnBasePos, spiralSpawnSymmetry, (spawnPos) -> {

                            DoublePoint basePos = new PolarVector(10, initAngle).add(spawnPos);
                            AbstractVector baseVelocity = new PolarVector(spiralSpeed, initAngle);

                            SpawnUtil.ringFormation(spawnPos, basePos, baseVelocity, spiralSymmetry, (p, v) -> SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseTurnAngle, spiralTurnAngularVelocity, (turnAngleAdd) ->{
                                    Angle turnAngle = v.getAngle().add(turnAngleAdd);
                                    spawnSpiralBullet(p, v, new PolarVector(spiralSpeed, turnAngle), sliceBoard);
                                }));
                        });
                    }));
                }

                if(spawnAimed){
                    double baseAngle = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID).nextDouble() * 360;

                    SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle, aimedAngularVelocity, (angle) -> {
                        DoublePoint basePos = new PolarVector(10, angle).add(pos);
                        AbstractVector baseVelocity = new PolarVector(aimedSpeed, angle);
                        SpawnUtil.ringFormation(pos, basePos, baseVelocity, aimedSymmetry, (p, v) -> spawnAimedBullet(p, v, aimedSpeed, sliceBoard));
                    });
                }
            }
        }

        private void spawnSpiralBullet(DoublePoint pos,
                                       AbstractVector velocity,
                                       AbstractVector finalVelocity,
                                       AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, ROSE, -30, 1)
                            .setProgram(ProgramUtil.makeSlowingAndSharpTurnBulletProgram(
                                    SPIRAL_PRE_SLOW_TIME,
                                    SPIRAL_SLOW_DURATION,
                                    SPIRAL_POST_SLOW_TIME,
                                    finalVelocity
                                    ).compile()
                            )
                            .packageAsMessage()
            );
        }

        private void spawnAimedBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      double finalSpeed,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, BLUE, -20, 0)
                            .setProgram(ProgramUtil.makeSlowingAndHomingBulletProgram(
                                    AIMED_PRE_SLOW_TIME,
                                    AIMED_SLOW_DURATION,
                                    AIMED_POST_SLOW_TIME,
                                    finalSpeed
                                    ).compile()
                            )
                            .packageAsMessage()
            );
        }
    }
}
