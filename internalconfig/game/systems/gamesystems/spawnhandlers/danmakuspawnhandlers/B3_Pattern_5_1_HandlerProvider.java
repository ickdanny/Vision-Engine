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
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.CartesianVector;
import util.math.geometry.ConstPolarVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B3_PATTERN_5_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B3_Pattern_5_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B3_Pattern_5_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                36,
                5,
                180,
                3.5,
                4,
                3.72,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                36,
                7,
                180,
                3.5,
                5,
                3.72,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                36,
                7,
                180,
                3.5,
                7,
                3.72,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                36,
                9,
                180,
                3.5,
                9,
                3.72,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int SHARP_WAIT_TIME = 8;
        private static final int SHARP_TURN_TIME = 33;

        private static final double SHARP_ANGLE_LOW = -320;
        private static final double SHARP_ANGLE_HIGH = -45;

        private static final double SHARP_STRAIGHT_SPEED_MULTI = .8;

        private final int mod;

        private final int largeSymmetry;
        private final double largeTotalAngle;
        private final double largeSpeed;

        private final int sharpSymmetry;
        private final double sharpSpeed;

        private final double sharpStraightSpeed;

        private Template(int mod,
                         int largeSymmetry,
                         double largeTotalAngle,
                         double largeSpeed,
                         int sharpSymmetry,
                         double sharpSpeed,
                         SpawnBuilder spawnBuilder) {
            super(B3_PATTERN_5_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.largeSymmetry = largeSymmetry;
            this.largeTotalAngle = largeTotalAngle;
            this.largeSpeed = largeSpeed;
            this.sharpSymmetry = sharpSymmetry;
            this.sharpSpeed = sharpSpeed;
            sharpStraightSpeed = sharpSpeed * SHARP_STRAIGHT_SPEED_MULTI;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
                DoublePoint basePos = new ConstPolarVector(15, angleToPlayer).add(pos);
                AbstractVector baseVelocity = new PolarVector(largeSpeed, angleToPlayer);

                SpawnUtil.arcFormation(
                        pos,
                        basePos,
                        baseVelocity,
                        largeSymmetry,
                        largeTotalAngle,
                        (p, v) -> spawnLargeBullet(p, v, sliceBoard)
                );

                SpawnUtil.arcFormation(
                        pos,
                        new DoublePoint(pos.getX() - 15, pos.getY()),
                        new CartesianVector(-sharpSpeed, 0),
                        sharpSymmetry,
                        90,
                        (p, v) -> {
                            spawnSharpBullet(p, new PolarVector(sharpStraightSpeed, v.getAngle()), randomFinalAngleLeft(random), VIOLET, sliceBoard);
                            spawnSharpBullet(p, v, randomFinalAngleLeft(random), SPRING, sliceBoard);
                        }
                );

                SpawnUtil.arcFormation(
                        pos,
                        new DoublePoint(pos.getX() + 15, pos.getY()),
                        new CartesianVector(sharpSpeed, 0),
                        sharpSymmetry,
                        90,
                        (p, v) -> {
                            spawnSharpBullet(p, new PolarVector(sharpStraightSpeed, v.getAngle()), randomFinalAngleRight(random), VIOLET, sliceBoard);
                            spawnSharpBullet(p, v, randomFinalAngleRight(random), SPRING, sliceBoard);
                        }
                );

            }
        }

        private Angle randomFinalAngleLeft(Random random){
            return new Angle(RandomUtil.randDoubleInclusive(SHARP_ANGLE_LOW, SHARP_ANGLE_HIGH, random));
        }

        private Angle randomFinalAngleRight(Random random){
            return new Angle(RandomUtil.randDoubleInclusive(180 - SHARP_ANGLE_HIGH, 180 - SHARP_ANGLE_LOW, random));
        }

        private void spawnSharpBullet(DoublePoint pos,
                                          AbstractVector velocity,
                                          Angle finalAngle,
                                          EnemyProjectileColors color,
                                          AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, color, NORMAL_OUTBOUND, 10)
                            .setProgram(ProgramUtil.makeShortTurnBulletProgram(SHARP_WAIT_TIME, finalAngle, SHARP_TURN_TIME).compile())
                            .packageAsMessage()
            );
        }

        private void spawnLargeBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, LARGE, BLUE, LARGE_OUTBOUND, -10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}