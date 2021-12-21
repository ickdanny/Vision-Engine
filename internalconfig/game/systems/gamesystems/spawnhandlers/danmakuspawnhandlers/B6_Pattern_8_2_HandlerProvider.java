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
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B6_PATTERN_8_2;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B6_Pattern_8_2_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B6_Pattern_8_2_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                8,
                1,
                2.5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                10,
                1.5,
                2.5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                12,
                1.8,
                2.5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                14,
                2.1,
                2.5,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int SLOW_DURATION = 67;
        private static final double ANGLE_BOUND = 5;

        private final int symmetry;
        private final double initSpeed;
        private final double finalSpeed;

        private Template(int symmetry,
                         double initSpeed,
                         double finalSpeed,
                         SpawnBuilder spawnBuilder) {
            super(B6_PATTERN_8_2, spawnBuilder, componentTypeContainer);
            this.symmetry = symmetry;
            this.initSpeed = initSpeed;
            this.finalSpeed = finalSpeed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            Random random = GameUtil.getRandom(globalBoard);

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
            double finalAngle = angleToPlayer + RandomUtil.randDoubleInclusive(-ANGLE_BOUND, ANGLE_BOUND, random);

            double baseInitAngle = random.nextDouble() * 360;

            AbstractVector baseVelocity = new PolarVector(initSpeed, baseInitAngle);
            DoublePoint basePos = new PolarVector(20, baseInitAngle).add(pos);

            AbstractVector finalVelocity = new PolarVector(finalSpeed, finalAngle);

            SpawnUtil.ringFormation(pos, basePos, baseVelocity, symmetry, (p, v) -> spawnBullet(p, v, finalVelocity, sliceBoard));
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector initVelocity,
                                 AbstractVector finalVelocity,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, initVelocity, LARGE, ORANGE, -100, -10)
                            .setProgram(makeProgram(finalVelocity))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeProgram(AbstractVector finalVelocity) {
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE
            ).linkInject(
                    ProgramBuilder.linearLink(
                            new InstructionNode<>(SLOW_TO_HALT, SLOW_DURATION),
                            new InstructionNode<>(SET_VELOCITY, finalVelocity)
                    )
            ).compile();
        }
    }
}