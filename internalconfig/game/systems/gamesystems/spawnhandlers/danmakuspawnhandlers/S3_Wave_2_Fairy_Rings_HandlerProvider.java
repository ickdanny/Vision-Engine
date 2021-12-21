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
import static internalconfig.game.components.spawns.DanmakuSpawns.S3_WAVE_2_FAIRY_RINGS;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S3_Wave_2_Fairy_Rings_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    private static final int MOD = S3_WAVE_2_FAIRY_RINGS.getDuration() / 3;

    public S3_Wave_2_Fairy_Rings_HandlerProvider(SpawnBuilder spawnBuilder,
                                                 AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    //SAME AS WAVE 7

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                10,
                3.6,
                3.6,
                1,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                12,
                3.6,
                3.6,
                1,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                14,
                3.6,
                3.6,
                1,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                16,
                3.6,
                3.6,
                1,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private final int symmetry;
        private final double speedLow;
        private final double speedHigh;
        private final int rows;

        private Template(int symmetry,
                         double speedLow,
                         double speedHigh,
                         int rows,
                         SpawnBuilder spawnBuilder) {
            super(S3_WAVE_2_FAIRY_RINGS, spawnBuilder, componentTypeContainer);
            this.symmetry = symmetry;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.rows = rows;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, MOD)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(ecsInterface.getGlobalBoard(), dataStorage, entityID);
                double angle = RandomUtil.randDoubleInclusive(0, 360, pseudoRandom);

                DoublePoint basePos = new PolarVector(10, angle).add(pos);
                SpawnUtil.columnFormation(speedLow, speedHigh, rows,
                        (speed) -> SpawnUtil.ringFormation(pos, basePos, new PolarVector(speed, angle), symmetry,
                                (p, v) -> spawnBullet(p, v, sliceBoard)));
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, ROSE, NORMAL_OUTBOUND, -2)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}