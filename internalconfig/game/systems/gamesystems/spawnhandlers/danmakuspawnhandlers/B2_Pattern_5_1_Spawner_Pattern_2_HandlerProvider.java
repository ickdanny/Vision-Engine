package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionVelocitySpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.ConstPolarVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.components.spawns.DanmakuSpawns.B2_PATTERN_5_1_SPAWNER_PATTERN_2;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B2_Pattern_5_1_Spawner_Pattern_2_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B2_Pattern_5_1_Spawner_Pattern_2_HandlerProvider(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(5,
                1,
                .5,
                3.9,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(5,
                1,
                .5,
                3.9,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(5,
                2,
                .5,
                3.9,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(5,
                2,
                .5,
                3.9,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionVelocitySpawnHandler {

        private final int mod;
        private final int symmetry;
        private final double angularVelocity;
        private final double speed;

        private Template(int mod, int symmetry, double angularVelocity, double speed, SpawnBuilder spawnBuilder) {
            super(B2_PATTERN_5_1_SPAWNER_PATTERN_2, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetry = symmetry;
            this.angularVelocity = angularVelocity;
            this.speed = speed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                double baseAngle = GameUtil.getVelocity(dataStorage, entityID, velocityComponentType).getAngle().getAngle() + 90;
                SpawnUtil.spiralFormation(tick, B2_PATTERN_5_1_SPAWNER_PATTERN_2.getDuration(), baseAngle, angularVelocity, (angle) -> {
                    AbstractVector baseVelocity = new PolarVector(speed, angle);
                    DoublePoint basePos = new ConstPolarVector(10, angle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, baseVelocity, symmetry, (p, v) -> spawnBullet(p, v, sliceBoard));
                });
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, SPRING, -200, -10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}
