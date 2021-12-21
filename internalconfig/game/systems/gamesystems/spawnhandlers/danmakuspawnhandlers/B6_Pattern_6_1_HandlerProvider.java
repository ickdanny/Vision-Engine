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
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B6_PATTERN_6_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B6_Pattern_6_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B6_Pattern_6_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    //SYMMETRY MUST BE EVEN

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                16,
                12,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                14,
                16,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                14,
                20,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                12,
                24,
                spawnBuilder
        );
    }

    protected class Template extends AbstractPositionSpawnHandler {

        private static final double SLOW_SPEED = 2.3;
        private static final double FAST_SPEED = 5;

        private static final int WAIT_TIME = 40;
        private static final int TURN_DURATION = 80;
        private static final int SPEED_DURATION = 40;

        private static final double OUTBOUND = -100;

        private final int mod;
        private final int symmetry;

        private Template(int mod,
                         int symmetry,
                         SpawnBuilder spawnBuilder) {
            super(B6_PATTERN_6_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetry = symmetry;
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

                AbstractVector baseInitVelocity = new PolarVector(SLOW_SPEED, baseAngle);
                DoublePoint basePos = new PolarVector(10, baseAngle).add(pos);
                SpawnUtil.ringFormation(pos, basePos, baseInitVelocity, symmetry, (p, initVelocity) -> {
                    AbstractVector finalVelocity = new PolarVector(FAST_SPEED, initVelocity.getAngle().add(179.9));
                    spawnBullet(p, initVelocity, finalVelocity, RED, sliceBoard);
                });

                baseAngle = posRandom.nextDouble() * 360;

                baseInitVelocity = new PolarVector(SLOW_SPEED, baseAngle);
                basePos = new PolarVector(10, baseAngle).add(pos);
                SpawnUtil.ringFormation(pos, basePos, baseInitVelocity, symmetry, (p, initVelocity) -> {
                    AbstractVector finalVelocity = new PolarVector(FAST_SPEED, initVelocity.getAngle().subtract(179.9));
                    spawnBullet(p, initVelocity, finalVelocity, BLUE, sliceBoard);
                });
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector initVelocity,
                                 AbstractVector finalVelocity,
                                 EnemyProjectileColors color,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, initVelocity, SHARP, color, OUTBOUND, 10)
                            .setProgram(makeArcProgram(finalVelocity))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeArcProgram(AbstractVector finalVelocity) {
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE,
                    new InstructionNode<>(TIMER, WAIT_TIME),
                    new InstructionNode<>(TURN_TO, new Tuple2<>(finalVelocity.getAngle(), TURN_DURATION)),
                    new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(finalVelocity, SPEED_DURATION))
            ).compile();
        }

    }
}