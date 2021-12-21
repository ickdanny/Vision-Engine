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
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S3_WAVE_1_SMOKEBALL_SPRAY;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S3_Wave_1_Smokeball_Spray_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S3_Wave_1_Smokeball_Spray_HandlerProvider(SpawnBuilder spawnBuilder,
                                                     AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    //shots = num random spawns

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                10,
                2,
                2,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                9,
                2,
                2,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                8,
                2,
                2,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                8,
                3,
                2,
                4,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private final int mod;
        private final int shots;
        private final double speedLow;
        private final double speedHigh;

        private Template(int mod,
                         int shots,
                         double speedLow,
                         double speedHigh,
                         SpawnBuilder spawnBuilder) {
            super(S3_WAVE_1_SMOKEBALL_SPRAY, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.shots = shots;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tick < 16 && tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                SpawnUtil.randomVelocities(
                        speedLow,
                        speedHigh,
                        0,
                        360,
                        shots,
                        random,
                        (velocity) -> spawnBullet(pos, velocity, sliceBoard)
                );
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, VIOLET, NORMAL_OUTBOUND, 10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

    }
}