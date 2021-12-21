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
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.NORMAL_OUTBOUND;
import static internalconfig.game.components.spawns.DanmakuSpawns.B5_PATTERN_1_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B5_Pattern_1_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B5_Pattern_1_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(2,
                2,
                1.5,
                2.5,
                .6 * 10,
                56,
                50,
                23,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(1,
                2,
                1.5,
                2.5,
                .6 * 10,
                56,
                50,
                23,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(1,
                3,
                1.5,
                2.5,
                .6 * 10,
                56,
                50,
                23,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(1,
                3,
                1.5,
                2.5,
                .6 * 10,
                56,
                50,
                43,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int SPIRAL_FORMATION_MAX_TICK = 413;

        private final int mod;

        private final int rows;
        private final double angularVelocity;
        private final double speedLow;
        private final double speedHigh;
        private final int waitTime;
        private final int turnTime;
        private final double angleOffsetRange;

        private Template(int mod,
                         int rows,
                         double speedLow,
                         double speedHigh,
                         double angularVelocity,
                         int waitTime,
                         int turnTime,
                         double angleOffsetRange,
                         SpawnBuilder spawnBuilder) {
            super(B5_PATTERN_1_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.rows = rows;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.angularVelocity = angularVelocity;
            this.waitTime = waitTime;
            this.turnTime = turnTime;
            this.angleOffsetRange = angleOffsetRange;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);
                Random random = GameUtil.getRandom(globalBoard);

                double baseAngle = pseudoRandom.nextDouble() * 360;

                SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle, angularVelocity, (angle) -> SpawnUtil.columnFormation(speedLow, speedHigh, rows, (speed) ->{
                    AbstractVector initVelocity = new PolarVector(speed, angle);
                    DoublePoint basePos = new PolarVector(10, angle).add(pos);
                    Angle finalAngle = angle.add(RandomUtil.randDoubleInclusive(-angleOffsetRange, angleOffsetRange, random));

                    spawnBullet(basePos, initVelocity, finalAngle, sliceBoard);
                }));
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector initVelocity,
                                 Angle finalAngle,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, initVelocity, SHARP, RED, NORMAL_OUTBOUND, 1)
                            .setProgram(ProgramUtil.makeShortTurnBulletProgram(waitTime, finalAngle, turnTime).compile())
                            .packageAsMessage()
            );
        }
    }
}
