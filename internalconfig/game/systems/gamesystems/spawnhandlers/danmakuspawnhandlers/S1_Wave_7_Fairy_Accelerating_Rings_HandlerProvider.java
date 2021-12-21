package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramUtil;
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
import static internalconfig.game.components.spawns.DanmakuSpawns.S1_WAVE_7_FAIRY_ACCELERATING_RINGS;

import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public class S1_Wave_7_Fairy_Accelerating_Rings_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S1_Wave_7_Fairy_Accelerating_Rings_HandlerProvider(SpawnBuilder spawnBuilder,
                                                              AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                3,
                7,
                2,
                7,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                2.5,
                7,
                3,
                10,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                2,
                7,
                4,
                14,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                2,
                7,
                5,
                17,
                spawnBuilder
        );
    }


    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private final int DURATION = S1_WAVE_7_FAIRY_ACCELERATING_RINGS.getDuration();
        private final static double ACCELERATION = .05;

        private final double speedLow;
        private final double speedHigh;
        private final int rows;
        private final int symmetry;

        private Template(double speedLow,
                         double speedHigh,
                         int rows,
                         int symmetry,
                         SpawnBuilder spawnBuilder) {
            super(S1_WAVE_7_FAIRY_ACCELERATING_RINGS, spawnBuilder, componentTypeContainer);
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.rows = rows;
            this.symmetry = symmetry;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, DURATION / 5) || (tick <= DURATION / 2 && tick != DURATION / 10 && tickMod(tick, DURATION / 10))) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                double baseAngle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
                DoublePoint basePos = new PolarVector(20, baseAngle).add(pos);
                SpawnUtil.columnFormation(speedLow, speedHigh, rows,
                        (speed) -> SpawnUtil.ringFormation(
                                pos,
                                basePos,
                                new PolarVector(speed, baseAngle),
                                symmetry,
                                (p, v) -> spawnBullet(p, v, sliceBoard)
                        ));
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(
                            pos,
                            new PolarVector(0, velocity.getAngle()),
                            MEDIUM,
                            RED,
                            NORMAL_OUTBOUND,
                            -2
                    )
                            .setProgram(ProgramUtil.makeAcceleratingBulletProgram(velocity.getMagnitude(), ACCELERATION).compile())
                            .packageAsMessage()
            );
        }
    }
}