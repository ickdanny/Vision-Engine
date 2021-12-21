package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B2_PATTERN_4_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B2_Pattern_4_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B2_Pattern_4_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                6,
                6,
                2.5,
                19,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                5,
                6,
                2.5,
                21,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                4,
                6,
                2.5,
                23,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                3,
                6,
                2.5,
                25,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int RING_MOD = 100;

        private static final int PRE_TIMER = 10;
        private static final int TURN_AND_SLOW_TIME = 80;
        private static final int ACCELERATE_TIME = 60;

        private static final double SLOWEST_SPEED = .5;
        private static final double MAX_ANGLE_SHIFT = 45;

        private static final double SPIRAL_SPEED = 4;
        private static final double RING_SPEED = 2.253;

        private final int spiralMod;
        private final int spiralSymmetry;
        private final double spiralAngularVelocity;

        private final int ringSymmetry;

        private Template(int spiralMod,
                         int spiralSymmetry,
                         double spiralAngularVelocity,
                         int ringSymmetry,
                         SpawnBuilder spawnBuilder) {
            super(B2_PATTERN_4_1, spawnBuilder, componentTypeContainer);
            this.spiralMod = spiralMod;
            this.spiralSymmetry = spiralSymmetry;
            this.spiralAngularVelocity = spiralAngularVelocity;
            this.ringSymmetry = ringSymmetry;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, spiralMod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double baseAngle = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID).nextDouble() * 360 - 54;
                SpawnUtil.spiralFormation(tick, B2_PATTERN_4_1.getDuration(), baseAngle, spiralAngularVelocity, (spiralAngle) -> {
                    AbstractVector baseVelocity = new PolarVector(SPIRAL_SPEED, spiralAngle);
                    DoublePoint basePos = new PolarVector(10, spiralAngle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, baseVelocity, spiralSymmetry,
                            (p, v) -> spawnSpiralBullet(
                                    p,
                                    v,
                                    RandomUtil.randDoubleInclusive(-MAX_ANGLE_SHIFT, MAX_ANGLE_SHIFT, random),
                                    sliceBoard
                            ));

                });
            }

            if (tickMod(tick, RING_MOD)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double baseAngle = RandomUtil.randDoubleInclusive(0, 360, random);

                DoublePoint basePos = new PolarVector(10, baseAngle).add(pos);
                SpawnUtil.ringFormation(pos, basePos, new PolarVector(RING_SPEED, baseAngle), ringSymmetry,
                        (p, v) -> spawnRingBullet(
                                p,
                                v,
                                sliceBoard
                        ));

            }
        }

        private void spawnSpiralBullet(DoublePoint pos,
                                       AbstractVector velocity,
                                       double angleShift,
                                       AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, VIOLET, NORMAL_OUTBOUND, 0)
                            .setProgram(makeSpiralProgram(velocity, angleShift))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeSpiralProgram(AbstractVector initVelocity, double angleShift) {
            Angle finalAngle = initVelocity.getAngle().add(angleShift);
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE
            ).linkAppend(
                    ProgramBuilder.linearLink(
                            new InstructionNode<>(TIMER, PRE_TIMER),
                            new InstructionNode<>(
                                    SLOW_DOWN_AND_TURN_TO_VELOCITY,
                                    new Tuple2<>(new PolarVector(SLOWEST_SPEED, finalAngle), TURN_AND_SLOW_TIME)
                            ),
                            new InstructionNode<>(
                                    SPEED_UP_TO_VELOCITY,
                                    new Tuple2<>(new PolarVector(initVelocity.getMagnitude(), finalAngle), ACCELERATE_TIME))
                    )
            ).compile();
        }

        private void spawnRingBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, BLUE, NORMAL_OUTBOUND, -1)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}