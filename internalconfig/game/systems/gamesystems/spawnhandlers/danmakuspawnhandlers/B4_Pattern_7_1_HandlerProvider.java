package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B4_PATTERN_7_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B4_Pattern_7_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B4_Pattern_7_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(7,
                30,
                -115,
                6,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(6,
                30,
                -125,
                7,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(5,
                30,
                -135,
                9,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(4,
                30,
                -145,
                10,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final double DIAGONAL_SPAWN_OUT = 40;

        private static final double DIAGONAL_SPEED_LOW = 1.9;
        private static final double DIAGONAL_SPEED_HIGH = 2.9;

        private static final double DIAGONAL_ANGLE_BOUND = 10;

        private static final double CIRCLE_SPEED = 1;
        private static final double CIRCLE_OUTWARD_SPEED = 2.1;
        private static final double CIRCLE_ANGLE_BOUND = 1;

        private static final int CIRCLE_WAIT_TIME = 60;

        private final int diagonalMod;
        private final int circleMod;

        private final double diagonalBaseAngle;
        private final int symmetry;

        private Template(int diagonalMod,
                         int circleMod,
                         double diagonalBaseAngle,
                         int symmetry,
                         SpawnBuilder spawnBuilder) {
            super(B4_PATTERN_7_1, spawnBuilder, componentTypeContainer);
            this.diagonalMod = diagonalMod;
            this.circleMod = circleMod;
            this.diagonalBaseAngle = diagonalBaseAngle;
            this.symmetry = symmetry;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            boolean spawnDiagonal = tickMod(tick, diagonalMod);
            boolean spawnCircle = tickMod(tick, circleMod);
            if (spawnDiagonal || spawnCircle) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

                Random random = GameUtil.getRandom(globalBoard);

                if (spawnDiagonal) {
                    spawnDiagonal(sliceBoard, random);
                }
                if (spawnCircle) {
                    AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                    DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                    double initBaseAngle = RandomUtil.randDoubleInclusive(-170, -10, random);
                    double finalBaseAngle = -180 - initBaseAngle;

                    AbstractVector circleVelocity = new PolarVector(CIRCLE_SPEED, initBaseAngle);

                    double spawnAngle = RandomUtil.randDoubleInclusive(0, 360, random);

                    DoublePoint basePos = new PolarVector(10, spawnAngle).add(pos);

                    AbstractVector initBaseVelocity = new PolarVector(CIRCLE_OUTWARD_SPEED, spawnAngle);
                    SpawnUtil.ringFormation(pos, basePos, initBaseVelocity, symmetry, (p, outVelocity) -> {
                        AbstractVector initVelocity = GeometryUtil.vectorAdd(circleVelocity, outVelocity);
                        double finalAngle = finalBaseAngle + RandomUtil.randDoubleInclusive(-CIRCLE_ANGLE_BOUND, CIRCLE_ANGLE_BOUND, random);
                        spawnCircleBullet(p, initVelocity, finalAngle, sliceBoard);
                    });
                }
            }
        }

        private void spawnDiagonal(AbstractPublishSubscribeBoard sliceBoard, Random random) {
            double spawn = RandomUtil.randDoubleInclusive(0, WIDTH + HEIGHT + (2 * DIAGONAL_SPAWN_OUT), random);
            DoublePoint spawnPos;
            if (spawn < WIDTH + DIAGONAL_SPAWN_OUT) {
                spawnPos = new DoublePoint(spawn, -DIAGONAL_SPAWN_OUT);
            } else {
                spawnPos = new DoublePoint(WIDTH + DIAGONAL_SPAWN_OUT, (spawn - WIDTH) - DIAGONAL_SPAWN_OUT);
            }
            double speed = RandomUtil.randDoubleInclusive(DIAGONAL_SPEED_LOW, DIAGONAL_SPEED_HIGH, random);
            double angle = diagonalBaseAngle + RandomUtil.randDoubleInclusive(-DIAGONAL_ANGLE_BOUND, DIAGONAL_SPEED_HIGH, random);
            AbstractVector velocity = new PolarVector(speed, angle);
            spawnDiagonalBullet(spawnPos, velocity, sliceBoard);
        }

        private void spawnDiagonalBullet(DoublePoint pos,
                                         AbstractVector velocity,
                                         AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, CYAN, -50, 5)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

        private void spawnCircleBullet(DoublePoint pos,
                                       AbstractVector velocity,
                                       double finalAngle,
                                       AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, GREEN, -100, 1)
                            .setProgram(
                                    ProgramUtil.makeSharpTurnBulletProgram(
                                            CIRCLE_WAIT_TIME,
                                            new PolarVector(CIRCLE_OUTWARD_SPEED, finalAngle)
                                    ).compile()
                            )
                            .packageAsMessage()
            );
        }
    }
}
