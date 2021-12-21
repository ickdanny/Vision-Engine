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

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B4_PATTERN_2_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B4_Pattern_2_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B4_Pattern_2_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                7,
                10,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                7,
                14,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                6,
                19,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                5,
                21,
                spawnBuilder
        );
    }

    protected class Template extends AbstractPositionSpawnHandler {

        private static final double BASE_ANGLE = 0;
        private static final double SPAWN_ANGULAR_VELOCITY = -4;

        private static final double DISTANCE = 50;

        private static final int WAIT_TIME = 1;
        private static final int SLOW_TIME = 7;

        private static final double INIT_SPEED = 10;
        private static final double FINAL_SPEED_LOW = 1;
        private static final double FINAL_SPEED_HIGH = 5.1;

        private final int mod;
        private final int spawns;

        private Template(int mod,
                         int spawns,
                         SpawnBuilder spawnBuilder) {
            super(B4_PATTERN_2_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.spawns = spawns;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                SpawnUtil.spiralFormation(tick, B4_PATTERN_2_1.getDuration(), BASE_ANGLE, SPAWN_ANGULAR_VELOCITY, (spawnAngle) -> {
                    DoublePoint spawnPos = new PolarVector(DISTANCE, spawnAngle).add(pos);
                    SpawnUtil.randomVelocities(FINAL_SPEED_LOW, FINAL_SPEED_HIGH, 0, 360d, spawns, random, (finalVelocity) -> {
                        DoublePoint basePos = new PolarVector(10, finalVelocity.getAngle()).add(spawnPos);
                        AbstractVector initVelocity = new PolarVector(INIT_SPEED, finalVelocity.getAngle());
                        spawnBullet(basePos, initVelocity, finalVelocity, sliceBoard);
                    });
                });
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 AbstractVector finalVelocity,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, GREEN, NORMAL_OUTBOUND, -1)
                            .setProgram(makeProgram(finalVelocity))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeProgram(AbstractVector velocity) {
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE,
                    new InstructionNode<>(TIMER, WAIT_TIME),
                    new InstructionNode<>(SLOW_DOWN_TO_VELOCITY, new Tuple2<>(velocity, SLOW_TIME))
            ).compile();
        }
    }
}
