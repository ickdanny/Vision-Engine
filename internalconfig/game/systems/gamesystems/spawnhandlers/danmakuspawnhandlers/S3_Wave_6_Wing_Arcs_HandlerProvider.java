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
import static internalconfig.game.components.spawns.DanmakuSpawns.S3_WAVE_6_WING_ARCS;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S3_Wave_6_Wing_Arcs_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S3_Wave_6_Wing_Arcs_HandlerProvider(SpawnBuilder spawnBuilder,
                                               AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                5,
                150,
                2.5,
                4.5,
                3,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                7,
                120,
                2.5,
                4.5,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                9,
                130,
                2.5,
                4.5,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                11,
                140,
                2.5,
                4.5,
                5,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private final int MOD = S3_WAVE_6_WING_ARCS.getDuration() / 3;

        private final int symmetry;
        private final double totalAngle;
        private final double speedLow;
        private final double speedHigh;
        private final int rows;

        private Template(int symmetry,
                         double totalAngle,
                         double speedLow,
                         double speedHigh,
                         int rows,
                         SpawnBuilder spawnBuilder) {
            super(S3_WAVE_6_WING_ARCS, spawnBuilder, componentTypeContainer);
            this.symmetry = symmetry;
            this.totalAngle = totalAngle;
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
                double angle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

                DoublePoint basePos = new PolarVector(10, angle).add(pos);

                SpawnUtil.columnFormation(speedLow, speedHigh, rows, (speed) -> {
                    AbstractVector baseVelocity = new PolarVector(speed, angle);
                    SpawnUtil.arcFormation(pos, basePos, baseVelocity, symmetry, totalAngle, (p, v) -> spawnBullet(p, v, sliceBoard));
                });
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, BLUE, NORMAL_OUTBOUND, -2)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}