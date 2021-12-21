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
import util.math.Constants;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S2_WAVE_4_WHEEL_COLUMNS;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public class S2_Wave_4_Wheel_Columns_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S2_Wave_4_Wheel_Columns_HandlerProvider(SpawnBuilder spawnBuilder,
                                                   AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(1.5,
                3,
                2,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(1.5,
                3.5,
                3,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(1.5,
                4,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(1.5,
                4.5,
                5,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private final int MOD = S2_WAVE_4_WHEEL_COLUMNS.getDuration() / 12;
        private final double ANGULAR_VELOCITY = (360d / Math.pow(Constants.PHI, 2)) / MOD;
        private final double TOTAL_ANGLE = 40;

        private final double speedLow;
        private final double speedHigh;
        private final int rows;

        private Template(double speedLow,
                         double speedHigh,
                         int rows,
                         SpawnBuilder spawnBuilder) {
            super(S2_WAVE_4_WHEEL_COLUMNS, spawnBuilder, componentTypeContainer);
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.rows = rows;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, MOD)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                double baseAngle = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID).nextDouble() * 360;

                SpawnUtil.spiralFormation(tick, S2_WAVE_4_WHEEL_COLUMNS.getDuration(), baseAngle, ANGULAR_VELOCITY,
                        (angle) -> SpawnUtil.columnFormation(speedLow, speedHigh, rows,
                                (speed) -> {
                                    AbstractVector velocity = new PolarVector(speed, angle);
                                    DoublePoint basePos = new PolarVector(10, angle).add(pos);

                                    SpawnUtil.arcFormation(pos, basePos, velocity, 2, TOTAL_ANGLE,
                                            (p, v) -> spawnBullet(p, v, sliceBoard)
                                    );
                                }
                        )
                );
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, CHARTREUSE, NORMAL_OUTBOUND, -2)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}