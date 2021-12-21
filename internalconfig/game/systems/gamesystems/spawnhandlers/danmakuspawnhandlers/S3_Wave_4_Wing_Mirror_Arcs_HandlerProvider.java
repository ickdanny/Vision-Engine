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
import static internalconfig.game.components.spawns.DanmakuSpawns.S3_WAVE_4_WING_MIRROR_ARCS;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S3_Wave_4_Wing_Mirror_Arcs_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S3_Wave_4_Wing_Mirror_Arcs_HandlerProvider(SpawnBuilder spawnBuilder,
                                                      AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(7,
                2,
                3,
                .5,
                2.2,
                1,
                0.881,
                6,
                102.142,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(6,
                2,
                3,
                .5,
                2.2,
                1,
                0.881,
                6,
                102.142,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(6,
                2,
                3,
                .5,
                2.2,
                2,
                0.881,
                6,
                102.142,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(5,
                2,
                3,
                .5,
                2.2,
                2,
                0.881,
                6,
                102.142,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private final int mod;
        private final double speedLow;
        private final double speedHigh;
        private final double speedMultiLow;
        private final double speedMultiHigh;
        private final int rows;
        private final double angularVelocity;
        private final int arcSymmetry;
        private final double totalAngle;

        private Template(int mod,
                         double speedLow,
                         double speedHigh,
                         double speedMultiLow,
                         double speedMultiHigh,
                         int rows,
                         double angularVelocity,
                         int arcSymmetry,
                         double totalAngle,
                         SpawnBuilder spawnBuilder) {
            super(S3_WAVE_4_WING_MIRROR_ARCS, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.speedMultiLow = speedMultiLow;
            this.speedMultiHigh = speedMultiHigh;
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
                SpawnUtil.whipFormation(tick, S3_WAVE_4_WING_MIRROR_ARCS.getDuration(), speedMultiLow, speedMultiHigh, (speedMulti) -> SpawnUtil.spiralFormation(tick, S3_WAVE_4_WING_MIRROR_ARCS.getDuration(), baseAngle, angularVelocity,
                        (angle) -> SpawnUtil.columnFormation(speedLow, speedHigh, rows, (speed) -> {

                            AbstractVector baseVelocity = new PolarVector(speed * speedMulti, angle);

                            DoublePoint basePos = new PolarVector(10, baseVelocity.getAngle()).add(pos);
                            SpawnUtil.arcFormation(pos, basePos, baseVelocity, arcSymmetry, totalAngle,
                                    (arcPos, arcVel) -> SpawnUtil.mirrorFormation(arcPos, arcVel, pos.getX(),
                                            (p, v) -> spawnBullet(p, v, sliceBoard)
                                    )
                            );

                        })
                ));
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