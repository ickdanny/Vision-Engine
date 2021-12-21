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
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B4_PATTERN_2_2;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B4_Pattern_2_2_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B4_Pattern_2_2_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                10,
                6,
                170,
                4.5,
                4.4,
                8,
                25,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                8,
                6,
                170,
                5,
                4.4,
                9,
                35,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                6,
                7,
                170,
                5.5,
                4.4,
                10,
                35,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                5,
                8,
                170,
                6.5,
                4.4,
                10,
                35,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final double BASE_ANGLE = 180;
        private static final double SPAWN_ANGULAR_VELOCITY = 4;

        private static final double DISTANCE = 50;

        private final static int AIMED_POST_SLOW_TIME = 5;

        private final int mod;
        private final int symmetry;
        private final double totalAngle;
        private final double initSpeed;
        private final double finalSpeed;
        private final int preSlowTime;
        private final int slowDuration;

        private Template(int mod,
                         int symmetry,
                         double totalAngle,
                         double initSpeed,
                         double finalSpeed,
                         int preSlowTime,
                         int slowDuration,
                         SpawnBuilder spawnBuilder) {
            super(B4_PATTERN_2_2, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetry = symmetry;
            this.totalAngle = totalAngle;
            this.initSpeed = initSpeed;
            this.finalSpeed = finalSpeed;
            this.preSlowTime = preSlowTime;
            this.slowDuration = slowDuration;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                SpawnUtil.spiralFormation(tick, B4_PATTERN_2_2.getDuration(), BASE_ANGLE, SPAWN_ANGULAR_VELOCITY, (spawnAngle) -> {
                    DoublePoint spawnPos = new PolarVector(DISTANCE, spawnAngle).add(pos);
                    Angle baseAngle = GeometryUtil.angleFromAToB(pos, spawnPos);
                    DoublePoint basePos = new PolarVector(10, baseAngle).add(spawnPos);
                    AbstractVector baseVelocity = new PolarVector(initSpeed, baseAngle);
                    SpawnUtil.arcFormation(spawnPos, basePos, baseVelocity, symmetry, totalAngle, (p, v) -> spawnBullet(p, v, finalSpeed, sliceBoard));
                });
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 double finalSpeed,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, YELLOW, NORMAL_OUTBOUND, 0)
                            .setProgram(ProgramUtil.makeSlowingAndHomingBulletProgram(
                                    preSlowTime,
                                    slowDuration,
                                    AIMED_POST_SLOW_TIME,
                                    finalSpeed
                                    ).compile()
                            )
                            .packageAsMessage()
            );
        }
    }
}
