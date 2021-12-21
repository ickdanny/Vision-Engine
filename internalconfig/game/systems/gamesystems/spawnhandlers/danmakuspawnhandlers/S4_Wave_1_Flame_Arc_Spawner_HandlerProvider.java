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
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.ConstPolarVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S4_WAVE_1_FLAME_ARC_SPAWNER;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S4_Wave_1_Flame_Arc_Spawner_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S4_Wave_1_Flame_Arc_Spawner_HandlerProvider(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                1,
                1,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                3,
                135,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                5,
                150,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                7,
                160,
                spawnBuilder
        );
    }

    private class Template extends AbstractPositionSpawnHandler {

        private static final int WAIT_TIME = 13;
        private static final int SLOW_TIME = 18;

        private final InstructionNode<?, ?>[] PROGRAM_1 =
                ProgramBuilder.linearLink(
                        new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                        SET_COLLIDABLE
                ).linkInject(
                        ProgramBuilder.linearLink(
                                new InstructionNode<>(TIMER, WAIT_TIME),
                                new InstructionNode<>(SLOW_TO_HALT, SLOW_TIME),
                                REMOVE_VISIBLE,
                                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.S4_WAVE_1_FLAME_ARC_SPAWNER_PATTERN_1),
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
                                new InstructionNode<>(TIMER, WAIT_TIME),
                                new InstructionNode<>(SLOW_TO_HALT, SLOW_TIME),
                                REMOVE_VISIBLE,
                                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.S4_WAVE_1_FLAME_ARC_SPAWNER_PATTERN_2),
                                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                REMOVE_ENTITY
                        )
                ).compile();

        private static final double SPEED = 3.5;

        private static final double ANGLE_BOUND = 30;

        private final int symmetry;
        private final double totalAngle;

        private Template(int symmetry, double totalAngle, SpawnBuilder spawnBuilder) {
            super(S4_WAVE_1_FLAME_ARC_SPAWNER, spawnBuilder, componentTypeContainer);
            this.symmetry = symmetry;
            this.totalAngle = totalAngle;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);

            if (tick == pseudoRandom.nextInt() % S4_WAVE_1_FLAME_ARC_SPAWNER.getDuration()) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                double baseAngle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
                baseAngle += RandomUtil.randDoubleInclusive(-ANGLE_BOUND, ANGLE_BOUND, random);

                DoublePoint basePos = new ConstPolarVector(10, baseAngle).add(pos);
                AbstractVector baseVelocity = new PolarVector(SPEED, baseAngle);

                AtomicBoolean colorBoolean = new AtomicBoolean(true);
                SpawnUtil.arcFormation(pos, basePos, baseVelocity, symmetry, totalAngle, (p, v) -> {
                    if(!colorBoolean.get()) {
                        spawnSpawner1(p, v, sliceBoard);
                    }
                    else{
                        spawnSpawner2(p, v, sliceBoard);
                    }
                    colorBoolean.set(!colorBoolean.get());
                });
            }
        }

        private void spawnSpawner1(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, ORANGE, NORMAL_OUTBOUND, 0)
                            .setProgram(PROGRAM_1)
                            .packageAsMessage()
            );
        }

        private void spawnSpawner2(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, YELLOW, NORMAL_OUTBOUND, 0)
                            .setProgram(PROGRAM_2)
                            .packageAsMessage()
            );
        }
    }
}
