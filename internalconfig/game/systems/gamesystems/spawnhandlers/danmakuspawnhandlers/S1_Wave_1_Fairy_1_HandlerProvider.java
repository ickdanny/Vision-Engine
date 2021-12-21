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

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S1_WAVE_1_FAIRY_OPENING_1;

import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public class S1_Wave_1_Fairy_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S1_Wave_1_Fairy_1_HandlerProvider(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                3,
                4,
                1,
                16,
                350,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                2.5,
                4.5,
                2,
                17,
                350,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                2.5,
                4.5,
                3,
                18,
                350,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                2,
                5,
                4,
                19,
                350,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
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
            super(S1_WAVE_1_FAIRY_OPENING_1, spawnBuilder, componentTypeContainer);
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
            double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
            double baseAngle = angleToPlayer + 180;
            SpawnUtil.columnFormation(speedLow, speedHigh, rows, (speed) -> {
                DoublePoint basePos = new PolarVector(15 + (5 * speed), baseAngle).add(pos);
                SpawnUtil.arcFormation(
                        pos,
                        basePos,
                        new PolarVector(speed, baseAngle),
                        symmetry,
                        totalAngle,
                        (p, v) -> spawnBullet(p, v, sliceBoard)
                );
            });
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, RED, NORMAL_OUTBOUND, 0)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

    }
}
