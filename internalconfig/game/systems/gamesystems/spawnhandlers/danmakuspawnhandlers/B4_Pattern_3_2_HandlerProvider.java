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

import static internalconfig.game.components.spawns.DanmakuSpawns.B4_PATTERN_3_2;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B4_Pattern_3_2_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B4_Pattern_3_2_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(25,
                18,
                .45,
                3.1243,
                20,
                85,
                11,
                .3,
                2.532,
                52,
                -90,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(20,
                10,
                .45,
                3.1243,
                20,
                85,
                13,
                .3,
                2.532,
                52,
                -90,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(17,
                12,
                .45,
                3.1243,
                20,
                85,
                14,
                .3,
                2.532,
                52,
                -90,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(16,
                13,
                .45,
                3.1243,
                20,
                85,
                15,
                .3,
                2.532,
                52,
                -90,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int SPIRAL_FORMATION_MAX_TICK = 413;

        private static final double FINAL_VELOCITY_SPEED_MULTI = 1.3;

        private final int mod;

        private final int symmetry1;
        private final double angularVelocity1;
        private final double speed1;
        private final int waitTime1;
        private final double angleOffset1;

        private final int symmetry2;
        private final double angularVelocity2;
        private final double speed2;
        private final int waitTime2;
        private final double angleOffset2;

        private Template(int mod,
                         int symmetry1,
                         double angularVelocity1,
                         double speed1,
                         int waitTime1,
                         double angleOffset1,
                         int symmetry2,
                         double angularVelocity2,
                         double speed2,
                         int waitTime2,
                         double angleOffset2,
                         SpawnBuilder spawnBuilder) {
            super(B4_PATTERN_3_2, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetry1 = symmetry1;
            this.angularVelocity1 = angularVelocity1;
            this.speed1 = speed1;
            this.waitTime1 = waitTime1;
            this.angleOffset1 = angleOffset1;
            this.symmetry2 = symmetry2;
            this.angularVelocity2 = angularVelocity2;
            this.speed2 = speed2;
            this.waitTime2 = waitTime2;
            this.angleOffset2 = angleOffset2;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);
                double baseAngle1 = pseudoRandom.nextDouble() * 360;
                double baseAngle2 = pseudoRandom.nextDouble() * 360;

                SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle1, angularVelocity1, (spiralAngle) -> {
                    AbstractVector initVelocity = new PolarVector(speed1, spiralAngle);
                    DoublePoint basePos = new PolarVector(10, spiralAngle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, initVelocity, symmetry1, (p, v) -> spawnBullet1(p, v, sliceBoard));
                });

                SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle2, angularVelocity2, (spiralAngle) -> {
                    AbstractVector initVelocity = new PolarVector(speed2, spiralAngle);
                    DoublePoint basePos = new PolarVector(10, spiralAngle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, initVelocity, symmetry2, (p, v) -> spawnBullet2(p, v, sliceBoard));
                });
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
