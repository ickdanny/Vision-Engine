package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B6_PATTERN_8_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B6_Pattern_8_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B6_Pattern_8_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    //SYMMETRY MUST BE EVEN

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                30,
                24,
                2.6,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                27,
                30,
                2.6,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                24,
                36,
                2.6,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                20,
                44,
                2.6,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private final int mod;
        private final int symmetry;
        private final double speed;

        private Template(int mod,
                         int symmetry,
                         double speed,
                         SpawnBuilder spawnBuilder) {
            super(B6_PATTERN_8_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetry = symmetry;
            this.speed = speed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random posRandom = GameUtil.getPseudoRandomBasedOnPosition(globalBoard, pos);

                double baseAngle = posRandom.nextDouble() * 360;

                if(tickMod(tick, mod * 2)){
                    baseAngle += GeometryUtil.fullAngleDivide(symmetry * 2);
                }

                AbstractVector baseVelocity = new PolarVector(speed, baseAngle);
                DoublePoint basePos = new PolarVector(10, baseAngle).add(pos);

                AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                SpawnUtil.ringFormation(pos, basePos, baseVelocity, symmetry, (p, v) -> {
                    EnemyProjectileTypes type = atomicBoolean.get() ? MEDIUM : SMALL;
                    int relativeDrawOrder = atomicBoolean.get() ? 0 : 1;
                    spawnBullet(p, v, type, relativeDrawOrder, sliceBoard);
                    atomicBoolean.set(!atomicBoolean.get());
                });
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 EnemyProjectileTypes type,
                                 int relativeDrawOrder,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, type, MAGENTA, NORMAL_OUTBOUND, relativeDrawOrder)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}