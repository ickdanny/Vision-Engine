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
import static internalconfig.game.components.spawns.DanmakuSpawns.S1_WAVE_9_WHEEL_SPIRAL;

import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public class S1_Wave_9_Wheel_Spiral_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S1_Wave_9_Wheel_Spiral_HandlerProvider(SpawnBuilder spawnBuilder,
                                                  AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(15,
                4,
                5,
                1,
                3,
                2.5 * (10d/15),
                2,
                50,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(10,
                4,
                5,
                1,
                3,
                2.5,
                2,
                50,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(10,
                4,
                5,
                1,
                3,
                2.5,
                3,
                50,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(10,
                3.5,
                5,
                2,
                3,
                2.5,
                3,
                50,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private final int mod;
        private final double speedLow;
        private final double speedHigh;
        private final int rows;
        private final int spiralSymmetry;
        private final double angularVelocity;
        private final int arcSymmetry;
        private final double totalAngle;

        private Template(int mod,
                         double speedLow,
                         double speedHigh,
                         int rows,
                         int spiralSymmetry,
                         double angularVelocity,
                         int arcSymmetry,
                         double totalAngle,
                         SpawnBuilder spawnBuilder) {
            super(S1_WAVE_9_WHEEL_SPIRAL, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.rows = rows;
            this.spiralSymmetry = spiralSymmetry;
            this.angularVelocity = angularVelocity;
            this.arcSymmetry = arcSymmetry;
            this.totalAngle = totalAngle;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                double baseAngle = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID).nextDouble() * 360;
                SpawnUtil.spiralFormation(tick, S1_WAVE_9_WHEEL_SPIRAL.getDuration(), baseAngle, angularVelocity,
                        (angle) -> SpawnUtil.columnFormation(speedLow, speedHigh, rows,
                                (speed) -> {
                                    AbstractVector baseVelocity = new PolarVector(speed, angle);
                                    SpawnUtil.ringFormation(pos, baseVelocity, spiralSymmetry,
                                            (ringPos, ringVelocity) -> {

                                                DoublePoint basePos = new PolarVector(10, ringVelocity.getAngle()).add(ringPos);
                                                SpawnUtil.arcFormation(ringPos, basePos, ringVelocity, arcSymmetry, totalAngle,
                                                        (p, v) -> spawnBullet(p, v, sliceBoard)
                                                );
                                            }
                                    );
                                }
                        )
                );
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, CYAN, NORMAL_OUTBOUND, -2)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}
