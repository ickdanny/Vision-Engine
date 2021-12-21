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

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S6_WAVE_2_APPARITION_COLUMNS;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S6_Wave_2_Apparition_Columns_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S6_Wave_2_Apparition_Columns_HandlerProvider(SpawnBuilder spawnBuilder,
                                                        AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                30,
                1.498364821 * (2d/3),
                2.075,
                3.92,
                2,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                27,
                1.498364821,
                2.075,
                4.22,
                3,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                23,
                1.498364821,
                2.075,
                4.22,
                3,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                20,
                1.498364821,
                2.075,
                4.22,
                3,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private static final int SYMMETRY = 5;

        private final int mod;
        private final double angularVelocity;
        private final double speedLow;
        private final double speedHigh;
        private final int rows;

        private Template(int mod,
                         double angularVelocity,
                         double speedLow,
                         double speedHigh,
                         int rows,
                         SpawnBuilder spawnBuilder) {
            super(S6_WAVE_2_APPARITION_COLUMNS, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.angularVelocity = angularVelocity;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.rows = rows;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();


                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random pseudoRandom = GameUtil.getPseudoRandomBasedOnDouble(globalBoard, pos.getY());

                double baseAngle = pseudoRandom.nextDouble() * 360;

                SpawnUtil.spiralFormation(tick, S6_WAVE_2_APPARITION_COLUMNS.getDuration(), baseAngle, angularVelocity, (angle) -> {
                    DoublePoint basePos = new PolarVector(10, angle).add(pos);

                    SpawnUtil.columnFormation(speedLow, speedHigh, rows, (speed) -> SpawnUtil.ringFormation(pos, basePos, new PolarVector(speed, angle), SYMMETRY, (p, v) -> spawnBullet(p, v, sliceBoard)));
                });
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, BLUE, NORMAL_OUTBOUND, 5)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}