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
import static internalconfig.game.components.spawns.DanmakuSpawns.S1_WAVE_2_SMOKEBALL_ARC;

import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public class S1_Wave_2_Smokeball_Arc_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    private static final int ANGLE_INCREMENT = 12;

    public S1_Wave_2_Smokeball_Arc_HandlerProvider(SpawnBuilder spawnBuilder,
                                                   AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                3.5,
                2,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                3.5,
                3,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                3.5,
                5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                3.5,
                7,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private final double speed;
        private final int symmetry;

        private Template(double speed, int symmetry, SpawnBuilder spawnBuilder) {
            super(S1_WAVE_2_SMOKEBALL_ARC, spawnBuilder, componentTypeContainer);
            this.speed = speed;
            this.symmetry = symmetry;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
            double baseAngle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

            DoublePoint basePos = new PolarVector(10, baseAngle).add(pos);
            AbstractVector baseVelocity = new PolarVector(speed, baseAngle);
            SpawnUtil.arcFormationIncrement(pos, basePos, baseVelocity, symmetry, ANGLE_INCREMENT,
                    (p, v) -> spawnBullet(p, v, sliceBoard));

        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, CHARTREUSE, NORMAL_OUTBOUND, -1)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

    }
}
