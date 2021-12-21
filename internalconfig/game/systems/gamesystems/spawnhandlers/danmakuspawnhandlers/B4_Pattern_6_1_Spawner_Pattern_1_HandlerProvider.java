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
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B4_PATTERN_6_1_SPAWNER_PATTERN_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B4_Pattern_6_1_Spawner_Pattern_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B4_Pattern_6_1_Spawner_Pattern_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                                            AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(15,
                6,
                0.8721,
                2.9,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(12,
                8,
                0.8721,
                2.9,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(12,
                9,
                0.8721,
                2.9,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(10,
                10,
                0.8721,
                2.9,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int SPIRAL_FORMATION_MAX_TICK = 413;

        private static final double OUTBOUND = -200;

        private final boolean[] TURNS = new boolean[]{true, false, true};

        private static final int WAIT_TIME = 25;

        private final int mod;
        private final int symmetry;
        private final double angularVelocity;
        private final double speed;

        private Template(int mod,
                         int symmetry,
                         double angularVelocity,
                         double speed,
                         SpawnBuilder spawnBuilder) {
            super(B4_PATTERN_6_1_SPAWNER_PATTERN_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetry = symmetry;
            this.angularVelocity = angularVelocity;
            this.speed = speed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);
                double baseAngle = pseudoRandom.nextDouble() * 360;

                SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle, angularVelocity, (angle) -> {
                    AbstractVector velocity = new PolarVector(speed, angle);
                    DoublePoint basePos = new PolarVector(10, angle).add(pos);

                    SpawnUtil.ringFormation(pos, basePos, velocity, symmetry, (p, v) -> spawnBullet(p, v, TURNS, sliceBoard));
                });

            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 boolean[] turns,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, YELLOW, OUTBOUND, 10)
                            .setProgram(makeProgram(velocity, turns))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeProgram(AbstractVector initVelocity, boolean[] turns) {
            if (turns.length > 0) {
                double speed = initVelocity.getMagnitude();
                Angle currentAngle = initVelocity.getAngle();
                ProgramBuilder.InstructionList instructionList = ProgramBuilder.linearLink(
                        new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                        SET_COLLIDABLE
                );

                if (turns[0]) {
                    currentAngle = currentAngle.add(90);
                } else {
                    currentAngle = currentAngle.subtract(90);
                }
                ProgramBuilder.InstructionList turnList = ProgramBuilder.linearLink(
                        new InstructionNode<>(TIMER, WAIT_TIME),
                        new InstructionNode<>(SET_VELOCITY, new PolarVector(speed, currentAngle))
                );

                for (int i = 1; i < turns.length; ++i) {
                    if (turns[i]) {
                        currentAngle = currentAngle.add(90);
                    } else {
                        currentAngle = currentAngle.subtract(90);
                    }
                    turnList = turnList.linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(TIMER, WAIT_TIME),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(speed, currentAngle))
                            )
                    );
                }

                instructionList.linkInject(turnList);
                return instructionList.compile();
            } else {
                throw new RuntimeException("turns <= 0!");
            }
        }
    }
}