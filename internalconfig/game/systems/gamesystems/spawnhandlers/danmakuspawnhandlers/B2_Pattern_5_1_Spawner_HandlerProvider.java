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
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.ConstPolarVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B2_PATTERN_4_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B2_Pattern_5_1_Spawner_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B2_Pattern_5_1_Spawner_HandlerProvider(SpawnBuilder spawnBuilder,
                                                  AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                14,
                2.2,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                12,
                2.1,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                12,
                2.1,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                9,
                2.2 * (8/9d),
                spawnBuilder
        );
    }

    private class Template extends AbstractPositionSpawnHandler {

        private static final double INIT_SPEED = 3;
        private static final int WAIT_TIME = 80;
        private static final int SLOW_TIME = WAIT_TIME - 2;

        private final InstructionNode<?, ?>[] PROGRAM_1 =
                ProgramBuilder.linearLink(
                        new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                        SET_COLLIDABLE
                ).linkInject(
                        ProgramBuilder.linearLink(
                                new InstructionNode<>(SLOW_TO_HALT, SLOW_TIME)
                        ).linkInject(
                                ProgramBuilder.linearLink(
                                        new InstructionNode<>(TIMER, WAIT_TIME),
                                        REMOVE_VISIBLE,
                                        new InstructionNode<>(SET_SPAWN, DanmakuSpawns.B2_PATTERN_5_1_SPAWNER_PATTERN_1),
                                        WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                        REMOVE_ENTITY
                                )
                        )
                ).compile();

        private final InstructionNode<?, ?>[] PROGRAM_2 =
                ProgramBuilder.linearLink(
                        new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                        SET_COLLIDABLE
                ).linkInject(
                        ProgramBuilder.linearLink(
                                new InstructionNode<>(SLOW_TO_HALT, SLOW_TIME)
                        ).linkInject(
                                ProgramBuilder.linearLink(
                                        new InstructionNode<>(TIMER, WAIT_TIME),
                                        REMOVE_VISIBLE,
                                        new InstructionNode<>(SET_SPAWN, DanmakuSpawns.B2_PATTERN_5_1_SPAWNER_PATTERN_2),
                                        WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                        REMOVE_ENTITY
                                )
                        )
                ).compile();

        private final int mod;
        private final double angularVelocity;

        public Template(int mod, double angularVelocity, SpawnBuilder spawnBuilder) {
            super(DanmakuSpawns.B2_PATTERN_5_1_SPAWNER, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.angularVelocity = angularVelocity;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if(tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double baseAngle = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID).nextDouble() * 360 - 54;
                SpawnUtil.spiralFormation(tick, B2_PATTERN_4_1.getDuration(), baseAngle, angularVelocity, (angle) -> {
                    DoublePoint basePos = new ConstPolarVector(10, angle).add(pos);

                    SpawnUtil.ringFormation(pos, basePos, new PolarVector(INIT_SPEED, angle), 2,
                            (p, v) -> spawnSpawner1(sliceBoard, p, v)
                    );

                    angle = angle.add(90);
                    basePos = new ConstPolarVector(10, angle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, new PolarVector(INIT_SPEED, angle), 2,
                            (p, v) -> spawnSpawner2(sliceBoard, p, v)
                    );

                });
            }
        }

        private void spawnSpawner1(AbstractPublishSubscribeBoard sliceBoard, DoublePoint pos, AbstractVector velocity) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, CYAN, -200, 0)
                            .setProgram(PROGRAM_1)
                            .packageAsMessage()
            );
        }

        private void spawnSpawner2(AbstractPublishSubscribeBoard sliceBoard, DoublePoint pos, AbstractVector velocity) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, SPRING, -200, 0)
                            .setProgram(PROGRAM_2)
                            .packageAsMessage()
            );
        }
    }
}