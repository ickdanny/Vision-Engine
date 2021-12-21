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
import util.math.geometry.ConstPolarVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.components.spawns.DanmakuSpawns.B2_PATTERN_3_1_SPAWNER_PATTERN_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B2_Pattern_3_1_Spawner_Pattern_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B2_Pattern_3_1_Spawner_Pattern_1_HandlerProvider(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    //SAME AS FOLLOWING

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(10,
                3,
                4.5,
                2.5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(8,
                3,
                4.7,
                2.5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(6,
                3,
                4.9,
                2.5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(5,
                3,
                5.1,
                2.5,
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
            super(B2_PATTERN_3_1_SPAWNER_PATTERN_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetry = symmetry;
            this.speed = speed;
            this.angularVelocity = angularVelocity;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                double baseAngle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

                SpawnUtil.spiralFormation(
                        B2_PATTERN_3_1_SPAWNER_PATTERN_1.getDuration() - tickExponentialFunction(tick),
                        B2_PATTERN_3_1_SPAWNER_PATTERN_1.getDuration(),
                        baseAngle,
                        angularVelocity,
                        (angle) -> {
                            AbstractVector baseVelocity = new PolarVector(speed, angle);
                            DoublePoint basePos = new ConstPolarVector(10, angle).add(pos);
                            SpawnUtil.ringFormation(pos, basePos, baseVelocity, symmetry, (p, v) -> spawnBullet(p, v, sliceBoard));
                        });
            }
        }

        private int tickExponentialFunction(int tick) {
            return (int) (Math.pow(2, (B2_PATTERN_3_1_SPAWNER_PATTERN_1.getDuration() - tick) / 15d)) - 1;
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, VIOLET, -200, 1)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}
