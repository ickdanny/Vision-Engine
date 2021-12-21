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
import static internalconfig.game.components.spawns.DanmakuSpawns.MB2_DEATH_SPIRAL;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class MB2_Death_Spiral_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public MB2_Death_Spiral_HandlerProvider(SpawnBuilder spawnBuilder,
                                            AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                5,
                8,
                9d/5,
                2,
                2,
                3,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                4,
                10,
                9d/4,
                3,
                2,
                3.5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                3,
                12,
                2.5,
                3,
                2,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                2,
                14,
                1.82,
                4,
                2,
                4.5,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private final int mod;
        private final int symmetry;
        private final double angularVelocity;
        private final int rows;
        private final double speedLow;
        private final double speedHigh;

        private Template(int mod,
                         int symmetry,
                         double angularVelocity,
                         int rows,
                         double speedLow,
                         double speedHigh,
                         SpawnBuilder spawnBuilder) {
            super(MB2_DEATH_SPIRAL, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetry = symmetry;
            this.angularVelocity = angularVelocity;
            this.rows = rows;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if(tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double baseAngle = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID).nextDouble() * 360;
                SpawnUtil.spiralFormation(tick, MB2_DEATH_SPIRAL.getDuration(), baseAngle, angularVelocity, (spiralAngle) -> SpawnUtil.columnFormation(speedLow, speedHigh, rows, (speed) -> {
                    AbstractVector baseVelocity = new PolarVector(speed, spiralAngle);
                    DoublePoint basePos = new PolarVector(10, spiralAngle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, baseVelocity, symmetry, (p, v) -> spawnBullet(
                            p,
                            v,
                            sliceBoard
                    ));
                }));
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, VIOLET, NORMAL_OUTBOUND, 0)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}