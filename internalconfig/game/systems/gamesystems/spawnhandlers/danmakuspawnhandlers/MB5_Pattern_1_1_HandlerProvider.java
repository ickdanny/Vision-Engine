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
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.MB5_PATTERN_1_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class MB5_Pattern_1_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public MB5_Pattern_1_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                           AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                30,
                .9,
                10,
                1.8,
                3,
                3,
                70,
                20,
                -35,
                30,
                25,
                1.5532,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                30,
                1.7,
                10,
                1.8,
                3,
                5,
                25,
                20,
                -45,
                30,
                30,
                1.5532,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                30,
                1.7,
                11,
                1.8,
                3,
                7,
                25,
                20,
                -45,
                30,
                35,
                1.5532,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                30,
                1.7,
                12,
                1.8,
                3,
                8,
                25,
                20,
                -45,
                30,
                40,
                1.5532,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private final int mod;
        private final double angularVelocity;
        private final int symmetry;
        private final double speedLow;
        private final double speedHigh;
        private final int rows;
        private final double speedAngleOffsetMulti;

        private final int waitTime;
        private final double angleOffset;
        private final int turnTime;

        private final int ringSymmetry;
        private final double ringSpeed;

        private Template(int mod,
                         double angularVelocity,
                         int symmetry,
                         double speedLow,
                         double speedHigh,
                         int rows,
                         double speedAngleOffsetMulti,
                         int waitTime,
                         double angleOffset,
                         int turnTime,
                         int ringSymmetry,
                         double ringSpeed,
                         SpawnBuilder spawnBuilder) {
            super(MB5_PATTERN_1_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.angularVelocity = angularVelocity;
            this.symmetry = symmetry;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.rows = rows;
            this.speedAngleOffsetMulti = speedAngleOffsetMulti;
            this.waitTime = waitTime;
            this.angleOffset = angleOffset;
            this.turnTime = turnTime;
            this.ringSymmetry = ringSymmetry;
            this.ringSpeed = ringSpeed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);
                double baseAngle = RandomUtil.randDoubleInclusive(0, Math.nextDown(360d), pseudoRandom);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                boolean side = tickMod(tick, mod * 2);
                EnemyProjectileColors color = side ? ROSE : MAGENTA;
                double sideSpeedAngleOffsetMulti = side ? speedAngleOffsetMulti : -speedAngleOffsetMulti;

                SpawnUtil.spiralFormation(tick, MB5_PATTERN_1_1.getDuration(), baseAngle, angularVelocity, (spiralAngle) -> SpawnUtil.columnFormation(speedLow, speedHigh, rows, (speed) -> {
                    double angle = spiralAngle.getAngle() + (speed * sideSpeedAngleOffsetMulti);
                    DoublePoint basePos = new PolarVector(10, angle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, new PolarVector(speed, angle), symmetry, (p, v) -> {
                        Angle finalAngle = side ? v.getAngle().add(angleOffset) : v.getAngle().subtract(angleOffset);
                        spawnCurveBullet(p, v, finalAngle, color, sliceBoard);
                    });
                }));

                if (tickMod(tick, mod * 3)) {
                    double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
                    AbstractVector baseVelocity = new PolarVector(ringSpeed, angleToPlayer);
                    DoublePoint basePos = new PolarVector(10, angleToPlayer).add(pos);

                    SpawnUtil.ringFormation(pos, basePos, baseVelocity, ringSymmetry, (p, v) -> spawnRingBullet(p, v, sliceBoard));
                }
            }
        }

        private void spawnCurveBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      Angle finalAngle,
                                      EnemyProjectileColors color,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, color, -100, 5)
                            .setProgram(ProgramUtil.makeShortTurnBulletProgram(waitTime, finalAngle, turnTime).compile())
                            .packageAsMessage()
            );
        }

        private void spawnRingBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, BLUE, NORMAL_OUTBOUND, 10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}