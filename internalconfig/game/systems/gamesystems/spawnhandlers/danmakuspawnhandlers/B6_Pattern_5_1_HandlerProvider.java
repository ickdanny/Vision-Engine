package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ProgramUtil;
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
import java.util.concurrent.atomic.AtomicBoolean;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B6_PATTERN_5_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static util.math.Constants.*;

public class B6_Pattern_5_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B6_Pattern_5_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(2,
                80,
                2,
                10.5,
                360/(PHI*2),
                135,
                15,
                8,
                2.8356,
                30,
                50,
                40,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(1,
                80,
                2,
                9.5,
                360/PHI,
                150,
                20,
                8,
                2.8356,
                40,
                60,
                50,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(1,
                80,
                3,
                9.5,
                360/PHI,
                165,
                25,
                10,
                2.8356,
                40,
                60,
                60,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(1,
                80,
                4,
                9.5,
                360/PHI,
                180,
                30,
                12,
                2.8356,
                40,
                60,
                70,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int SPIRAL_FORMATION_MAX_TICK = 413;

        private static final double ARC_SLOW_SPEED = 1.3;
        private static final double ARC_FAST_SPEED = 3.2;

        private static final int ARC_SPEED_DURATION = 90;

        private final int arcMod;
        private final int largeMod;

        private final int arcSymmetry;
        private final double arcTotalAngle;
        private final double arcAngularVelocity;
        private final int arcWaitTime;
        private final double arcAngleRange;

        private final int largeSymmetry;
        private final double largeSpeed;
        private final int largeWaitTime;
        private final int largeTurnTime;
        private final double largeAngleOffset;

        private Template(int arcMod,
                         int largeMod,
                         int arcSymmetry,
                         double arcTotalAngle,
                         double arcAngularVelocity,
                         int arcWaitTime,
                         double arcAngleRange,
                         int largeSymmetry,
                         double largeSpeed,
                         int largeWaitTime,
                         int largeTurnTime,
                         double largeAngleOffset,
                         SpawnBuilder spawnBuilder) {
            super(B6_PATTERN_5_1, spawnBuilder, componentTypeContainer);
            this.arcMod = arcMod;
            this.largeMod = largeMod;
            this.arcSymmetry = arcSymmetry;
            this.arcTotalAngle = arcTotalAngle;
            this.arcAngularVelocity = arcAngularVelocity;
            this.arcWaitTime = arcWaitTime;
            this.arcAngleRange = arcAngleRange;
            this.largeSymmetry = largeSymmetry;
            this.largeSpeed = largeSpeed;
            this.largeWaitTime = largeWaitTime;
            this.largeTurnTime = largeTurnTime;
            this.largeAngleOffset = largeAngleOffset;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, arcMod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);
                Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double baseAngle = pseudoRandom.nextDouble() * 360;

                SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle, arcAngularVelocity, (angle) -> {
                    AbstractVector baseInitVelocity = new PolarVector(ARC_SLOW_SPEED, angle);
                    DoublePoint basePos = new PolarVector(10, angle).add(pos);
                    double angleOffset = RandomUtil.randDoubleInclusive(-arcAngleRange, arcAngleRange, random);
                    SpawnUtil.arcFormation(pos, basePos, baseInitVelocity, arcSymmetry, arcTotalAngle, (p, initVelocity) -> {
                        Angle finalAngle = initVelocity.getAngle().add(angleOffset);
                        AbstractVector finalVelocity = new PolarVector(ARC_FAST_SPEED, finalAngle);
                        spawnArcBullet(p, initVelocity, finalVelocity, sliceBoard);
                    });
                });
            }
            if (tickMod(tick, largeMod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double baseAngle = random.nextDouble() * 360;

                DoublePoint basePos = new PolarVector(10, baseAngle).add(pos);
                AbstractVector baseVelocity = new PolarVector(largeSpeed, baseAngle);

                AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                SpawnUtil.ringFormation(pos, basePos, baseVelocity, largeSymmetry, (p, v) -> {
                    double angleOffset = atomicBoolean.get() ? largeAngleOffset : -largeAngleOffset;
                    Angle finalAngle = v.getAngle().add(angleOffset);
                    spawnLargeBullet(p, v, finalAngle, sliceBoard);
                    atomicBoolean.set(!atomicBoolean.get());
                });
            }
        }

        private void spawnArcBullet(DoublePoint pos,
                                    AbstractVector initVelocity,
                                    AbstractVector finalVelocity,
                                    AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, initVelocity, SHARP, YELLOW, NORMAL_OUTBOUND, 10)
                            .setProgram(makeArcProgram(finalVelocity))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeArcProgram(AbstractVector finalVelocity) {
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE,
                    new InstructionNode<>(TIMER, arcWaitTime),
                    new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(finalVelocity, ARC_SPEED_DURATION))
            ).compile();
        }

        private void spawnLargeBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      Angle finalAngle,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, LARGE, ORANGE, LARGE_OUTBOUND, -10)
                            .setProgram(ProgramUtil.makeShortTurnBulletProgram(largeWaitTime, finalAngle, largeTurnTime).compile())
                            .packageAsMessage()
            );
        }
    }
}