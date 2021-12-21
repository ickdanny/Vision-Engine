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
import static internalconfig.game.components.spawns.DanmakuSpawns.S4_WAVE_15_SPIKE_RING;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S4_Wave_15_Spike_Ring_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S4_Wave_15_Spike_Ring_HandlerProvider(SpawnBuilder spawnBuilder,
                                                 AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                40,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                61,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                72,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                84,
                spawnBuilder
        );
    }

    private class Template extends AbstractPositionSpawnHandler {

        private static final double SPEED = 2.9;

        private final int symmetry;

        private Template(int symmetry,
                         SpawnBuilder spawnBuilder) {
            super(S4_WAVE_15_SPIKE_RING, spawnBuilder, componentTypeContainer);
            this.symmetry = symmetry;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(ecsInterface.getGlobalBoard(), dataStorage, entityID);
            double angle = RandomUtil.randDoubleInclusive(0, 360, pseudoRandom);

            DoublePoint basePos = new PolarVector(10, angle).add(pos);
            SpawnUtil.ringFormation(pos, basePos, new PolarVector(SPEED, angle), symmetry, (p, v) -> spawnBullet(p, v, sliceBoard));
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, RED, NORMAL_OUTBOUND, 0)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}