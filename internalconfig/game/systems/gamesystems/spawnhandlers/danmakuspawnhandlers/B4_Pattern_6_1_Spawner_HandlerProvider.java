package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.spawns.DanmakuSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B4_PATTERN_6_1_SPAWNER;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B4_Pattern_6_1_Spawner_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B4_Pattern_6_1_Spawner_HandlerProvider(SpawnBuilder spawnBuilder,
                                                  AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                40,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                40,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                40,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                40,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private static final double BOUNDARY = 10;

        private static final double Y_AVG = WIDTH / 2D + 34;
        private static final double Y_GAUSSIAN_MULTI = 50;

        private static final double SPEED = 2.463;

        private final InstructionNode<?, ?>[] PROGRAM_1 =
                ProgramBuilder.linearLink(
                        new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                        SET_COLLIDABLE
                ).linkInject(
                        ProgramBuilder.linearLink(
                                new InstructionNode<>(BOUNDARY_X, BOUNDARY),
                                SET_COLLIDABLE,
                                new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0)),
                                REMOVE_VISIBLE,
                                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.B4_PATTERN_6_1_SPAWNER_PATTERN_1),
                                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                REMOVE_ENTITY
                        )
                ).compile();

        private final InstructionNode<?, ?>[] PROGRAM_2 =
                ProgramBuilder.linearLink(
                        new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                        SET_COLLIDABLE
                ).linkInject(
                        ProgramBuilder.linearLink(
                                new InstructionNode<>(BOUNDARY_X, BOUNDARY),
                                SET_COLLIDABLE,
                                new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0)),
                                REMOVE_VISIBLE,
                                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.B4_PATTERN_6_1_SPAWNER_PATTERN_2),
                                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                REMOVE_ENTITY
                        )
                ).compile();

        private final int mod;

        private Template(int mod, SpawnBuilder spawnBuilder) {
            super(B4_PATTERN_6_1_SPAWNER, spawnBuilder, componentTypeContainer);
            this.mod = mod;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if(tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(ecsInterface.getGlobalBoard());

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double y1 = Y_AVG + random.nextGaussian() * Y_GAUSSIAN_MULTI;
                double y2 = Y_AVG + random.nextGaussian() * Y_GAUSSIAN_MULTI;

                DoublePoint target1 = new DoublePoint(BOUNDARY, y1);
                DoublePoint target2 = new DoublePoint(WIDTH - BOUNDARY, y2);

                Angle angle1 = GeometryUtil.angleFromAToB(pos, target1);
                Angle angle2 = GeometryUtil.angleFromAToB(pos, target2);

                AbstractVector velocity1 = new PolarVector(SPEED, angle1);
                AbstractVector velocity2 = new PolarVector(SPEED, angle2);

                spawnSpawner(pos, velocity1, PROGRAM_1, sliceBoard);
                spawnSpawner(pos, velocity2, PROGRAM_2, sliceBoard);
            }
        }

        private void spawnSpawner(DoublePoint pos,
                                  AbstractVector velocity,
                                  InstructionNode<?, ?>[] program,
                                  AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, CYAN, NORMAL_OUTBOUND, 0)
                            .setProgram(program)
                            .packageAsMessage()
            );
        }
    }
}