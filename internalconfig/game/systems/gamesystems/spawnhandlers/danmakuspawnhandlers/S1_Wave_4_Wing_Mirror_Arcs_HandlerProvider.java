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
import static internalconfig.game.components.spawns.DanmakuSpawns.S1_WAVE_4_WING_MIRROR_ARCS;

import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public class S1_Wave_4_Wing_Mirror_Arcs_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S1_Wave_4_Wing_Mirror_Arcs_HandlerProvider(SpawnBuilder spawnBuilder,
                                                      AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(15,
                2.8,
                3.8,
                1,
                1.2,
                4,
                50,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(15,
                2.8,
                3.8,
                2,
                1.2,
                4,
                50,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(15,
                2.4,
                3.8,
                3,
                1.2,
                4,
                50,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(12,
                2,
                3.8,
                4,
                1.2,
                4,
                50,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private final int mod;
        private final double speedLow;
        private final double speedHigh;
        private final int rows;
        private final double angularVelocity;
        private final int arcSymmetry;
        private final double totalAngle;

        private Template(int mod,
                         double speedLow,
                         double speedHigh,
                         int rows,
                         double angularVelocity,
                         int arcSymmetry,
                         double totalAngle,
                         SpawnBuilder spawnBuilder) {
            super(S1_WAVE_4_WING_MIRROR_ARCS, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.rows = rows;
            this.angularVelocity = angularVelocity;
            this.arcSymmetry = arcSymmetry;
            this.totalAngle = totalAngle;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                double baseAngle = -90;
                SpawnUtil.spiralFormation(tick, S1_WAVE_4_WING_MIRROR_ARCS.getDuration(), baseAngle, angularVelocity,
                        (angle) -> SpawnUtil.columnFormation(speedLow, speedHigh, rows, (speed) -> {

                            AbstractVector baseVelocity = new PolarVector(speed, angle);

                            DoublePoint basePos = new PolarVector(10, baseVelocity.getAngle()).add(pos);
                            SpawnUtil.arcFormation(pos, basePos, baseVelocity, arcSymmetry, totalAngle,
                                    (arcPos, arcVel) -> SpawnUtil.mirrorFormation(arcPos, arcVel, pos.getX(),
                                            (p, v) -> spawnBullet(p, v, sliceBoard)
                                    )
                            );

                        })
                );
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
