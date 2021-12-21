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
import util.math.geometry.Angle;
import util.math.geometry.ConstPolarVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.components.spawns.DanmakuSpawns.B2_PATTERN_1_2;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.GameConfig.*;

public class B2_Pattern_1_2_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    private static final double ANGLE_DIFF = -6;

    public B2_Pattern_1_2_HandlerProvider(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(6,
                5,
                4.5,
                -5.183719,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(5,
                5,
                4.5,
                -4.183719,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(4,
                5,
                4.5,
                -4.183719,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(3,
                5,
                4.5,
                -4.183719,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private final int mod;
        private final int symmetry;
        private final double speed;
        private final double angularVelocity;

        private Template(int mod, int symmetry, double speed, double angularVelocity, SpawnBuilder spawnBuilder) {
            super(B2_PATTERN_1_2, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetry = symmetry;
            this.speed = speed;
            this.angularVelocity = angularVelocity;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                double baseAngle = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID).nextDouble() * 360;
                SpawnUtil.spiralFormation(tick, B2_PATTERN_1_2.getDuration(), baseAngle, angularVelocity, (angle) -> {
                    AbstractVector baseVelocity = new PolarVector(speed, angle);
                    DoublePoint basePos = new ConstPolarVector(10, angle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, baseVelocity, symmetry, (p, v) -> spawnMediumBullet(p, v, sliceBoard));

                    angle = new Angle(angle.getAngle() + ANGLE_DIFF);
                    baseVelocity = new PolarVector(speed, angle);
                    basePos = new ConstPolarVector(2, angle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, baseVelocity, symmetry, (p, v) -> spawnSmallBullet(p, v, sliceBoard));

                });
            }
        }

        private void spawnMediumBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, SPRING, NORMAL_OUTBOUND, 10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

        private void spawnSmallBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, SPRING, NORMAL_OUTBOUND, 11)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}
