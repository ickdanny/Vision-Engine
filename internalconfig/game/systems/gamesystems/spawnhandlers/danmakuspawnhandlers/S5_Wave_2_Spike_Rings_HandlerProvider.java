package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S5_WAVE_2_SPIKE_RINGS;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S5_Wave_2_Spike_Rings_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S5_Wave_2_Spike_Rings_HandlerProvider(SpawnBuilder spawnBuilder,
                                                      AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                16,
                1.7,
                8,
                2,
                3.6,
                4,
                20,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                15,
                1.7,
                10,
                2,
                3.6,
                6,
                20,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                13,
                1.7,
                11,
                2,
                3.6,
                7,
                20,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                12,
                1.7,
                12,
                2,
                3.6,
                8,
                20,
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

        private Template(int mod,
                         double angularVelocity,
                         int symmetry,
                         double speedLow,
                         double speedHigh,
                         int rows,
                         double speedAngleOffsetMulti,
                         SpawnBuilder spawnBuilder) {
            super(S5_WAVE_2_SPIKE_RINGS, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.angularVelocity = angularVelocity;
            this.symmetry = symmetry;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.rows = rows;
            this.speedAngleOffsetMulti = speedAngleOffsetMulti;
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
                EnemyProjectileColors color = side ? RED : BLUE;
                double sideSpeedAngleOffsetMulti = side ? speedAngleOffsetMulti : -speedAngleOffsetMulti;

                SpawnUtil.spiralFormation(tick, S5_WAVE_2_SPIKE_RINGS.getDuration(), baseAngle, angularVelocity, (spiralAngle) -> SpawnUtil.columnFormation(speedLow, speedHigh, rows, (speed) -> {
                    double angle = spiralAngle.getAngle() + (speed * sideSpeedAngleOffsetMulti);
                    DoublePoint basePos = new PolarVector(10, angle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, new PolarVector(speed, angle), symmetry, (p, v) -> spawnBullet(p, v, color, sliceBoard));
                }));
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 EnemyProjectileColors color,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, color, NORMAL_OUTBOUND, 5)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}