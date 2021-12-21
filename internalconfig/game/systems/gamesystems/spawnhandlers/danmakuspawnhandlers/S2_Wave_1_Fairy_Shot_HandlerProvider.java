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
import static internalconfig.game.components.spawns.DanmakuSpawns.S2_WAVE_1_FAIRY_SHOT;

import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public class S2_Wave_1_Fairy_Shot_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S2_Wave_1_Fairy_Shot_HandlerProvider(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(5,
                6.4,
                3,
                1,
                0,
                0,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(5,
                6.4,
                4,
                1,
                2,
                31,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(4.5,
                6.9,
                5,
                1,
                2,
                27,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(4,
                7.4,
                6,
                3,
                4,
                20,
                spawnBuilder
        );
    }

    private class Template extends AbstractPositionSpawnHandler {


        private final double speedLow;
        private final double speedHigh;
        private final int mediumRows;

        private final int mediumSymmetry;
        private final int smallSymmetry;

        private final double angleIncrement;

        private Template(double speedLow,
                         double speedHigh,
                         int mediumRows,
                         int mediumSymmetry,
                         int smallSymmetry,
                         double angleIncrement,
                         SpawnBuilder spawnBuilder) {
            super(S2_WAVE_1_FAIRY_SHOT, spawnBuilder, componentTypeContainer);
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.mediumRows = mediumRows;
            this.mediumSymmetry = mediumSymmetry;
            this.smallSymmetry = smallSymmetry;
            this.angleIncrement = angleIncrement;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
            double baseAngle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
            DoublePoint basePos = new PolarVector(10, baseAngle).add(pos);

            SpawnUtil.columnFormation(speedLow, speedHigh, mediumRows,
                    (speed) -> SpawnUtil.arcFormationIncrement(pos, basePos, new PolarVector(speed, baseAngle), mediumSymmetry, angleIncrement,
                            (p, v) -> spawnMediumBullet(p, v, sliceBoard)));

            if(smallSymmetry > 0) {
                double speedDifference = speedHigh - speedLow;
                double speedRatio = 1d/(mediumRows + 1);
                double speedMargin = speedDifference * speedRatio;

                double smallSpeedLow = speedLow + speedMargin;
                double smallSpeedHigh = speedHigh - speedMargin;

                SpawnUtil.columnFormation(smallSpeedLow, smallSpeedHigh, mediumRows - 1,
                        (speed) -> SpawnUtil.arcFormationIncrement(pos, basePos, new PolarVector(speed, baseAngle), smallSymmetry, angleIncrement,
                                (p, v) -> spawnSmallBullet(p, v, sliceBoard)));
            }
        }

        private void spawnMediumBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, VIOLET, NORMAL_OUTBOUND, -1)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

        private void spawnSmallBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, VIOLET, NORMAL_OUTBOUND, 2)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

    }
}
