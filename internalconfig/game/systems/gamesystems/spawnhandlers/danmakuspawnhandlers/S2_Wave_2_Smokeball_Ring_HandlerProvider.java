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
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S2_WAVE_2_SMOKEBALL_RING;

import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public class S2_Wave_2_Smokeball_Ring_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S2_Wave_2_Smokeball_Ring_HandlerProvider(SpawnBuilder spawnBuilder,
                                                    AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                6,
                1,
                4,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                6,
                1,
                4,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                6,
                1,
                4,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                6,
                1,
                4,
                4,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private final int symmetry;
        private final int rows;
        private final double speedLow;
        private final double speedHigh;

        private Template(int symmetry,
                         int rows,
                         double speedLow,
                         double speedHigh,
                         SpawnBuilder spawnBuilder) {
            super(S2_WAVE_2_SMOKEBALL_RING, spawnBuilder, componentTypeContainer);
            this.symmetry = symmetry;
            this.rows = rows;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            Random random = GameUtil.getRandom(globalBoard);
            double angle = RandomUtil.randDoubleInclusive(0, Math.nextDown(360d), random);

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
            DoublePoint basePos = new PolarVector(10, angle).add(pos);

            SpawnUtil.columnFormation(speedLow, speedHigh, rows,
                    (speed) -> SpawnUtil.ringFormation(pos, basePos, new PolarVector(speed, angle), symmetry,
                            (p, v) -> spawnBullet(p, v, sliceBoard)));

        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, BLUE, NORMAL_OUTBOUND, 2)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

    }
}