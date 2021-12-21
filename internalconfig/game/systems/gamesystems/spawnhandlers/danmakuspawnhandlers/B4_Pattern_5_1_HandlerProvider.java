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
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.components.spawns.DanmakuSpawns.B4_PATTERN_5_1;
import static internalconfig.game.components.spawns.DanmakuSpawns.B4_PATTERN_5_2;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B4_Pattern_5_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B4_Pattern_5_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    //SAME AS BELOW

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(22,
                6,
                15,
                2,
                10,
                -.25,
                3.1243,
                20,
                85,
                12,
                2.532,
                52,
                -90,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(18,
                5,
                15,
                2,
                12,
                -.25,
                3.1243,
                20,
                85,
                14,
                2.532,
                52,
                -90,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(15,
                4,
                15,
                2,
                14,
                -.25,
                3.1243,
                20,
                85,
                16,
                2.532,
                52,
                -90,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(13,
                3,
                15,
                2,
                15,
                -.25,
                3.1243,
                20,
                85,
                17,
                2.532,
                52,
                -90,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int SPIRAL_FORMATION_MAX_TICK = 413;

        private final int TICK_PLACEHOLDER = B4_PATTERN_5_2.getDuration();

        private static final double SINE_TICK_MULTI = 1/150d;
        private static final double SINE_ANGLE_BOUND = 25;

        private static final double FINAL_VELOCITY_SPEED_MULTI = 1.3;

        private final int mod1;
        private final int mod2;

        private final int tickDivisor2;
        private final int tickOverMod2;

        private final int symmetry1;
        private final double angularVelocity1;
        private final double speed1;
        private final int waitTime1;
        private final double angleOffset1;


        private final int symmetry2;
        private final double speed2;
        private final int waitTime2;
        private final double angleOffset2;

        private Template(int mod1,
                         int mod2,
                         int tickDivisor2,
                         int tickOverMod2,
                         int symmetry1,
                         double angularVelocity1,
                         double speed1,
                         int waitTime1,
                         double angleOffset1,
                         int symmetry2,
                         double speed2,
                         int waitTime2,
                         double angleOffset2,
                         SpawnBuilder spawnBuilder) {
            super(B4_PATTERN_5_1, spawnBuilder, componentTypeContainer);
            this.mod1 = mod1;
            this.mod2 = mod2;
            this.tickDivisor2 = tickDivisor2;
            this.tickOverMod2 = tickOverMod2;
            this.symmetry1 = symmetry1;
            this.angularVelocity1 = angularVelocity1;
            this.speed1 = speed1;
            this.waitTime1 = waitTime1;
            this.angleOffset1 = angleOffset1;
            this.symmetry2 = symmetry2;
            this.speed2 = speed2;
            this.waitTime2 = waitTime2;
            this.angleOffset2 = angleOffset2;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            boolean spawn1 = tickMod(tick, mod1);
            boolean spawn2 = tickMod(tick, mod2) && tickMod(tick/tickDivisor2, tickOverMod2);
            if (spawn1 || spawn2) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);
                double baseAngle1 = pseudoRandom.nextDouble() * 360;
                double baseAngle2 = pseudoRandom.nextDouble() * 360;

                if(spawn1) {
                    SpawnUtil.spiralFormation(TICK_PLACEHOLDER, SPIRAL_FORMATION_MAX_TICK, baseAngle1, angularVelocity1, (spiralAngle) -> {
                        AbstractVector initVelocity = new PolarVector(speed1, spiralAngle);
                        DoublePoint basePos = new PolarVector(10, spiralAngle).add(pos);
                        SpawnUtil.ringFormation(pos, basePos, initVelocity, symmetry1, (p, v) -> spawnBullet1(p, v, sliceBoard));
                    });
                }

                if(spawn2) {
                    SpawnUtil.sineFormation(TICK_PLACEHOLDER, baseAngle2, SINE_TICK_MULTI, SINE_ANGLE_BOUND, (sineAngle) -> {
                        AbstractVector initVelocity = new PolarVector(speed2, sineAngle);
                        DoublePoint basePos = new PolarVector(10, sineAngle).add(pos);
                        SpawnUtil.ringFormation(pos, basePos, initVelocity, symmetry2, (p, v) -> spawnBullet2(p, v, sliceBoard));
                    });
                }
            }
        }

        private void spawnBullet1(DoublePoint pos,
                                  AbstractVector velocity,
                                  AbstractPublishSubscribeBoard sliceBoard) {
            AbstractVector finalVelocity = new PolarVector(speed1 * FINAL_VELOCITY_SPEED_MULTI, velocity.getAngle().add(angleOffset1));
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, CYAN, -100, -1)
                            .setProgram(ProgramUtil.makeSharpTurnBulletProgram(waitTime1, finalVelocity).compile())
                            .packageAsMessage()
            );
        }

        private void spawnBullet2(DoublePoint pos,
                                  AbstractVector velocity,
                                  AbstractPublishSubscribeBoard sliceBoard) {
            AbstractVector finalVelocity = new PolarVector(speed2 * FINAL_VELOCITY_SPEED_MULTI, velocity.getAngle().add(angleOffset2));
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, GREEN, -100, 1)
                            .setProgram(ProgramUtil.makeSharpTurnBulletProgram(waitTime2, finalVelocity).compile())
                            .packageAsMessage()
            );
        }
    }
}
