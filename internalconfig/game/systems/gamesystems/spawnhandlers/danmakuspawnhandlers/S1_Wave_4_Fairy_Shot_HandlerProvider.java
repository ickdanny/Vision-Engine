package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.NORMAL_OUTBOUND;
import static internalconfig.game.components.spawns.DanmakuSpawns.S1_WAVE_4_FAIRY_SHOT;

import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public class S1_Wave_4_Fairy_Shot_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {
    private static final AbstractSpawnHandler SPAWN_NOTHING_HANDLER = new AbstractSpawnHandler() {
        @Override
        public Spawns getSpawn() {
            return S1_WAVE_4_FAIRY_SHOT;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        }
    };

    private static final double TOTAL_WIDTH = 12;

    public S1_Wave_4_Fairy_Shot_HandlerProvider(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return SPAWN_NOTHING_HANDLER;
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return SPAWN_NOTHING_HANDLER;
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(3, 4, 1, spawnBuilder);
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(3, 4, 2, spawnBuilder);
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private final double speedLow;
        private final double speedHigh;
        private final int rows;

        private Template(double speedLow,
                         double speedHigh,
                         int rows,
                         SpawnBuilder spawnBuilder) {
            super(S1_WAVE_4_FAIRY_SHOT, spawnBuilder, componentTypeContainer);
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.rows = rows;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tick == 1) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                double baseAngle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
                DoublePoint basePos = new PolarVector(10, baseAngle).add(pos);

                SpawnUtil.blockFormation(basePos, new Angle(baseAngle + 90), TOTAL_WIDTH, 2,
                        (p) -> SpawnUtil.columnFormation(speedLow, speedHigh, rows,
                                (speed) -> spawnBullet(p, new PolarVector(speed, baseAngle), sliceBoard)));
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, ORANGE, NORMAL_OUTBOUND, 2)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}
