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
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B4_PATTERN_4_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public class B4_Pattern_4_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B4_Pattern_4_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(12,
                3,
                40,
                2.5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(10,
                4,
                40,
                2.5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(8,
                4,
                50,
                2.5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(6,
                4,
                60,
                2.5,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int SPIRAL_FORMATION_MAX_TICK = 413;

        private static final double SPAWN_DIST = 25;

        private static final double ANGULAR_VELOCITY = -.03 * 7;//.3

        private static final double OUTBOUND = -200;

        private static final int SLOW_TIME = 20;
        private static final int SPEED_DURATION = 30;

        private final int mod;
        private final int turnNum;
        private final int waitTime;
        private final double speed;

        private Template(int mod,
                         int turnNum,
                         int waitTime,
                         double speed,
                         SpawnBuilder spawnBuilder) {
            super(B4_PATTERN_4_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.turnNum = turnNum;
            this.waitTime = waitTime;
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
                double baseAngle1 = pseudoRandom.nextDouble() * 360;

                boolean[] turns = makeTurns(tick, turnNum);

                SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle1, ANGULAR_VELOCITY, (angle) -> {
                    AbstractVector velocity = new PolarVector(speed, angle);
                    DoublePoint basePos = new PolarVector(SPAWN_DIST, angle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, velocity, 2, (p, v) -> spawnBullet(p, v, turns, MAGENTA, sliceBoard));

                    angle = angle.add(90);
                    velocity = new PolarVector(speed, angle);
                    basePos = new PolarVector(SPAWN_DIST, angle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, velocity, 2, (p, v) -> spawnBullet(p, v, turns, GREEN, sliceBoard));
                });

            }
        }

        private boolean[] makeTurns(int tick, int turnNum) {
            boolean[] turns = new boolean[turnNum];
            for (int i = 0; i < turnNum; ++i) {
                turns[i] = tickMod(tick / (int) (Math.pow(2, i + 1) * 2), 2);
            }
            return turns;
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 boolean[] turns,
                                 EnemyProjectileColors color,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, color, OUTBOUND, -1)
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
                        new InstructionNode<>(TIMER, waitTime),
                        new InstructionNode<>(SET_VELOCITY, new PolarVector(0, currentAngle)),
                        new InstructionNode<>(TIMER, SLOW_TIME),
                        new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(new PolarVector(speed, currentAngle), SPEED_DURATION))
                );

                for (int i = 1; i < turns.length; ++i) {
                    if (turns[i]) {
                        currentAngle = currentAngle.add(90);
                    } else {
                        currentAngle = currentAngle.subtract(90);
                    }
                    turnList = turnList.linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(TIMER, waitTime),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, currentAngle)),
                                    new InstructionNode<>(TIMER, SLOW_TIME),
                                    new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(new PolarVector(speed, currentAngle), SPEED_DURATION))
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
