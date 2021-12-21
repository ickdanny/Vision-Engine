package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
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
import util.tuple.Tuple2;

import java.util.concurrent.atomic.AtomicBoolean;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B5_PATTERN_2_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B5_Pattern_2_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B5_Pattern_2_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                                    AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                16,
                9,
                2,
                15,
                .4,
                3.7,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                18,
                11,
                2,
                15,
                .4,
                3.7,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                18,
                13,
                3,
                10,
                .4,
                3.7,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                20,
                15,
                3,
                10,
                .4,
                3.7,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private static final int WAIT_TIME = 60 * 8 + 23;
        private static final int ACCELERATE_DURATION = 127;

        private final int symmetry;
        private final int rows;
        private final int arcSymmetry;
        private final int totalAngle;
        private final double speedLow;
        private final double speedHigh;

        private Template(int symmetry,
                         int rows,
                         int arcSymmetry,
                         int totalAngle,
                         double speedLow,
                         double speedHigh,
                         SpawnBuilder spawnBuilder) {
            super(B5_PATTERN_2_1, spawnBuilder, componentTypeContainer);
            this.symmetry = symmetry;
            this.rows = rows;
            this.arcSymmetry = arcSymmetry;
            this.totalAngle = totalAngle;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
            double angle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

            AtomicBoolean bool = new AtomicBoolean(false);
            SpawnUtil.columnFormation(speedLow, speedHigh, rows, (speed) -> {
                DoublePoint basePos = new PolarVector((speed * 10) + 5, angle).add(pos);
                SpawnUtil.ringFormation(pos, basePos, new PolarVector(speed, angle), symmetry, (ringPos, ringVel) -> SpawnUtil.arcFormation(pos, ringPos, ringVel, bool.get() ? arcSymmetry : arcSymmetry - 1, totalAngle, (p, v) -> spawnBullet(p, v, sliceBoard)));
                bool.set(!bool.get());
            });
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, MAGENTA, NORMAL_OUTBOUND, 10)
                            .setProgram(makeProgram(velocity))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeProgram(AbstractVector initVelocity){
            AbstractVector finalVelocity = new PolarVector(initVelocity.getMagnitude() + 3, initVelocity.getAngle());
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, 5),
                    SET_COLLIDABLE,
                    new InstructionNode<>(TIMER, WAIT_TIME),
                    new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(finalVelocity, ACCELERATE_DURATION))
            ).compile();
        }
    }
}