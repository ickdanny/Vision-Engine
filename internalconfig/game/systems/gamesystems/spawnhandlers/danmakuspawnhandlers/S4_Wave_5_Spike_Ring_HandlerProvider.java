package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
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
import java.util.concurrent.atomic.AtomicBoolean;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S4_WAVE_5_SPIKE_RING;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S4_Wave_5_Spike_Ring_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S4_Wave_5_Spike_Ring_HandlerProvider(SpawnBuilder spawnBuilder,
                                                AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                22,
                2,
                4,
                3,
                33,
                29,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                28,
                1.8,
                4,
                5,
                37,
                31,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                36,
                1.8,
                4,
                5,
                42,
                33,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                44,
                1.8,
                4,
                5,
                47,
                34,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private static final int WAIT_TIME = 30;

        private final int symmetry;
        private final double speedLow;
        private final double speedHigh;
        private final int rows;

        private final double turn;
        private final int turnTime;

        private Template(int symmetry,
                         double speedLow,
                         double speedHigh,
                         int rows,
                         double turn,
                         int turnTime,
                         SpawnBuilder spawnBuilder) {
            super(S4_WAVE_5_SPIKE_RING, spawnBuilder, componentTypeContainer);
            this.symmetry = symmetry;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.rows = rows;
            this.turn = turn;
            this.turnTime = turnTime;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(ecsInterface.getGlobalBoard(), dataStorage, entityID);
            double angle = RandomUtil.randDoubleInclusive(0, 360, pseudoRandom);

            DoublePoint basePos = new PolarVector(10, angle).add(pos);
            SpawnUtil.columnFormation(speedLow, speedHigh, rows,
                    (speed) -> {
                        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
                        SpawnUtil.ringFormation(pos, basePos, new PolarVector(speed, angle), symmetry,
                                (p, v) -> {
                                    if(atomicBoolean.get()) {
                                        spawnBullet(p, v, v.getAngle().add(turn), ORANGE, 0, sliceBoard);
                                    }
                                    else{
                                        spawnBullet(p, v, v.getAngle().subtract(turn), RED, 1, sliceBoard);
                                    }
                                    atomicBoolean.set(!atomicBoolean.get());
                                });
                    });
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 Angle finalAngle,
                                 EnemyProjectileColors color,
                                 int relativeDrawOrder,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, color, NORMAL_OUTBOUND, relativeDrawOrder)
                            .setProgram(ProgramUtil.makeShortTurnBulletProgram(WAIT_TIME, finalAngle, turnTime).compile())
                            .packageAsMessage()
            );
        }
    }
}