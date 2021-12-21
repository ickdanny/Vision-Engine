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

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B6_PATTERN_7_1_SPAWNER;
import static internalconfig.game.components.spawns.DanmakuSpawns.B6_PATTERN_4_1_SPAWNER_PATTERN_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B6_Pattern_7_1_Spawner_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B6_Pattern_7_1_Spawner_HandlerProvider(SpawnBuilder spawnBuilder,
                                                  AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                7,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                8,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                10,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                12,
                spawnBuilder
        );
    }

    protected class Template extends AbstractPositionSpawnHandler {

        private final InstructionNode<?, ?>[] SPAWNER_PROGRAM = ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE,
                new InstructionNode<>(SET_SPAWN, B6_PATTERN_4_1_SPAWNER_PATTERN_1)
        ).compile();

        private static final double SPEED = 2.8;

        private final int symmetry;

        public Template(int symmetry,
                        SpawnBuilder spawnBuilder) {
            super(B6_PATTERN_7_1_SPAWNER, spawnBuilder, componentTypeContainer);
            this.symmetry = symmetry;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            Random random = GameUtil.getRandom(globalBoard);

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            double baseAngle = random.nextDouble() * 360;

            AbstractVector baseVelocity = new PolarVector(SPEED, baseAngle);
            DoublePoint basePos = new PolarVector(10, baseAngle).add(pos);

            SpawnUtil.ringFormation(pos, basePos, baseVelocity, symmetry, (p, v) -> spawnSpawner(p, v, sliceBoard));
        }

        private void spawnSpawner(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, MAGENTA, -50, 0)
                            .setProgram(SPAWNER_PROGRAM)
                            .packageAsMessage()
            );
        }
    }
}
