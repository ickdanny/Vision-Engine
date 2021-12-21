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
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S4_WAVE_8_APPARITION_COLUMNS;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S4_Wave_8_Apparition_Columns_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S4_Wave_8_Apparition_Columns_HandlerProvider(SpawnBuilder spawnBuilder,
                                                      AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                10,
                3,
                5.292,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                8,
                3,
                5.292,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                6,
                4,
                5.292,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                5,
                5,
                5.292,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private static final double ANGLE_OFFSET = 19.4;

        private final int mod;
        private final int symmetry;
        private final double speed;

        private Template(int mod,
                         int symmetry,
                         double speed,
                         SpawnBuilder spawnBuilder) {
            super(S4_WAVE_8_APPARITION_COLUMNS, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetry = symmetry;
            this.speed = speed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);
                double angle = RandomUtil.randDoubleInclusive(0, Math.nextDown(360d), random);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                DoublePoint basePos = new PolarVector(10, angle).add(pos);

                SpawnUtil.ringFormation(pos, basePos, new PolarVector(speed, angle), symmetry,
                        (p, v) -> spawnBullet(p, v, sliceBoard)
                );

                angle += ANGLE_OFFSET;
                basePos = new PolarVector(10, angle).add(pos);

                SpawnUtil.ringFormation(pos, basePos, new PolarVector(speed, angle), symmetry,
                        (p, v) -> spawnBullet(p, v, sliceBoard)
                );
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, ROSE, NORMAL_OUTBOUND, 0)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}