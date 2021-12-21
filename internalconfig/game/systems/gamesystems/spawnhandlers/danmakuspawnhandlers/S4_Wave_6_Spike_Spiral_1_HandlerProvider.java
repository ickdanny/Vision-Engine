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
import static internalconfig.game.components.spawns.DanmakuSpawns.S4_WAVE_6_SPIKE_SPIRAL_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S4_Wave_6_Spike_Spiral_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S4_Wave_6_Spike_Spiral_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                                    AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                5,
                2.87,
                6,
                3.91,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                4,
                2.87,
                6,
                3.91,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                3,
                2.87,
                6,
                3.91,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                2,
                2.87,
                6,
                3.91,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private final int mod;
        private final double speed;
        private final int spiralSymmetry;
        private final double angularVelocity;

        private Template(int mod,
                         double speed,
                         int spiralSymmetry,
                         double angularVelocity,
                         SpawnBuilder spawnBuilder) {
            super(S4_WAVE_6_SPIKE_SPIRAL_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.speed = speed;
            this.spiralSymmetry = spiralSymmetry;
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
                SpawnUtil.spiralFormation(tick, S4_WAVE_6_SPIKE_SPIRAL_1.getDuration(), baseAngle, angularVelocity,
                        (angle) -> {
                            AbstractVector baseVelocity = new PolarVector(speed, angle);
                            DoublePoint basePos = new PolarVector(10, angle).add(pos);
                            SpawnUtil.ringFormation(pos, basePos, baseVelocity, spiralSymmetry,
                                    (p, v) -> spawnBullet(p, v, sliceBoard)
                            );
                        }
                );
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, RED, NORMAL_OUTBOUND, -2)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}