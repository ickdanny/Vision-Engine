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
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S5_WAVE_3_SPIKE_SHOT;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S5_Wave_3_Spike_Shot_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S5_Wave_3_Spike_Shot_HandlerProvider(SpawnBuilder spawnBuilder,
                                                 AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                14,
                5.1,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                11,
                5.6,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                9,
                6,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                7,
                6.5,
                spawnBuilder
        );
    }

    private class Template extends AbstractPositionSpawnHandler {

        private final int mod;
        private final double speed;

        private Template(int mod,
                         double speed,
                         SpawnBuilder spawnBuilder) {
            super(S5_WAVE_3_SPIKE_SHOT, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.speed = speed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

                AbstractVector velocity = new PolarVector(speed, angleToPlayer);
                DoublePoint basePos = new PolarVector(10, angleToPlayer).add(pos);

                spawnBullet(basePos, velocity, sliceBoard);
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, BLUE, NORMAL_OUTBOUND, -1)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}