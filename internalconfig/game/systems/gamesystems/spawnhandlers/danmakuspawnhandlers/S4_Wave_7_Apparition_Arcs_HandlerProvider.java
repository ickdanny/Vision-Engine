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
import static internalconfig.game.components.spawns.DanmakuSpawns.S4_WAVE_7_APPARITION_ARCS;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S4_Wave_7_Apparition_Arcs_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S4_Wave_7_Apparition_Arcs_HandlerProvider(SpawnBuilder spawnBuilder,
                                                      AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                32,
                8,
                142,
                3.12,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                30,
                10,
                142,
                3.12,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                25,
                12,
                142,
                3.12,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                20,
                14,
                142,
                3.12,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private final int mod;
        private final int symmetry;
        private final double totalAngle;
        private final double speed;

        private Template(int mod,
                         int symmetry,
                         double totalAngle,
                         double speed,
                         SpawnBuilder spawnBuilder) {
            super(S4_WAVE_7_APPARITION_ARCS, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetry = symmetry;
            this.totalAngle = totalAngle;
            this.speed = speed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                double angle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
                DoublePoint basePos = new PolarVector(10, angle).add(pos);

                SpawnUtil.arcFormation(pos, basePos, new PolarVector(speed, angle), symmetry, totalAngle,
                        (p, v) -> spawnBullet(p, v, sliceBoard)
                );
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, AZURE, NORMAL_OUTBOUND, 0)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

    }
}