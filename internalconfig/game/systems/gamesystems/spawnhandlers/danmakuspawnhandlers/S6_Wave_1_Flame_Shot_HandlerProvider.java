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
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S6_WAVE_1_FLAME_SHOT;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S6_Wave_1_Flame_Shot_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S6_Wave_1_Flame_Shot_HandlerProvider(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(20,
                2.1,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(15,
                2.1,
                4.3,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(12,
                2.1,
                4.6,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(10,
                2.1,
                4.9,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private final int mod;
        private final double speedLow;
        private final double speedHigh;

        private Template(int mod,
                         double speedLow,
                         double speedHigh,
                         SpawnBuilder spawnBuilder) {
            super(S6_WAVE_1_FLAME_SHOT, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if(tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double angle = -90;

                DoublePoint basePos = new PolarVector(10, angle).add(pos);

                double speed = RandomUtil.randDoubleInclusive(speedLow, speedHigh, random);
                AbstractVector baseVelocity = new PolarVector(speed, angle);

                EnemyProjectileColors color = random.nextBoolean() ? YELLOW : ORANGE;
                spawnBullet(basePos, baseVelocity, color, sliceBoard);
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 EnemyProjectileColors color,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, color, NORMAL_OUTBOUND, 0)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}
