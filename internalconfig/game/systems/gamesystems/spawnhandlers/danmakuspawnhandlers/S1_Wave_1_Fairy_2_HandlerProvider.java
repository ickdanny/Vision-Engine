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
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.NORMAL_OUTBOUND;
import static internalconfig.game.components.spawns.DanmakuSpawns.S1_WAVE_1_FAIRY_OPENING_2;

import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public class S1_Wave_1_Fairy_2_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S1_Wave_1_Fairy_2_HandlerProvider(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                4,
                6,
                2,
                2,
                12,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                4.3,
                6.3,
                3,
                2,
                13,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                3.6,
                6.6,
                3,
                3,
                14,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                3,
                7,
                4,
                4,
                15,
                spawnBuilder
        );
    }

    private class Template extends AbstractPositionSpawnHandler {

        private final double speedLow;
        private final double speedHigh;
        private final int rows;
        private final int symmetry;
        private final double totalAngle;

        private Template(double speedLow,
                         double speedHigh,
                         int rows,
                         int symmetry,
                         double totalAngle,
                         SpawnBuilder spawnBuilder) {
            super(S1_WAVE_1_FAIRY_OPENING_2, spawnBuilder, componentTypeContainer);
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.rows = rows;
            this.symmetry = symmetry;
            this.totalAngle = totalAngle;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
            double baseAngle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
            DoublePoint basePos = new PolarVector(20, baseAngle).add(pos);
            SpawnUtil.columnFormation(speedLow, speedHigh, rows, (speed) ->
                    SpawnUtil.arcFormation(
                            pos,
                            basePos,
                            new PolarVector(speed, baseAngle),
                            symmetry,
                            totalAngle,
                            (p, v) -> spawnBullet(p, v, sliceBoard)
                    ));
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, ORANGE, NORMAL_OUTBOUND, 0)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

    }
}
