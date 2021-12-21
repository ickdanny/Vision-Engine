package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
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
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B6_PATTERN_2_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class B6_Pattern_2_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B6_Pattern_2_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(7,
                80,
                3.24124,
                2.83,
                4,
                2.1,
                -45,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(5,
                75,
                4.24124,
                2.83,
                5,
                2.1,
                -45,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(4,
                60,
                4.24124,
                2.83,
                6,
                2.1,
                -45,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(3,
                50,
                4.24124,
                2.83,
                7,
                2.1,
                -45,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {


        private static final int SPIRAL_FORMATION_MAX_TICK = 413;

        private static final int SHARP_SYMMETRY = 3;
        private static final double SHARP_BASE_ANGLE = -90;

        private static final double LARGE_Y_RANGE = 50;

        private final int sharpMod;
        private final int largeMod;

        private final double sharpAngularVelocity;
        private final double sharpSpeed;

        private final int largeSpawns;
        private final double largeSpeed;
        private final double largeAngle;

        private Template(int sharpMod,
                         int largeMod,
                         double sharpAngularVelocity,
                         double sharpSpeed,
                         int largeSpawns,
                         double largeSpeed,
                         double largeAngle,
                         SpawnBuilder spawnBuilder) {
            super(B6_PATTERN_2_1, spawnBuilder, componentTypeContainer);
            this.sharpMod = sharpMod;
            this.largeMod = largeMod;
            this.sharpAngularVelocity = sharpAngularVelocity;
            this.sharpSpeed = sharpSpeed;
            this.largeSpawns = largeSpawns;
            this.largeSpeed = largeSpeed;
            this.largeAngle = largeAngle;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, sharpMod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, SHARP_BASE_ANGLE, sharpAngularVelocity, (angle) -> {
                    AbstractVector baseVelocity = new PolarVector(sharpSpeed, angle);
                    DoublePoint basePos = new PolarVector(10, angle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, baseVelocity, SHARP_SYMMETRY, (p, v) -> {
                        spawnSharpBullet(p, v, MAGENTA, sliceBoard);
                        SpawnUtil.singleSideMirror(p, v, pos.getX(), (mirrorPos, mirrorVelocity) -> spawnSharpBullet(mirrorPos, mirrorVelocity, CYAN, sliceBoard));
                    });
                });
            }
            if (tickMod(tick, largeMod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

                Random random = GameUtil.getRandom(globalBoard);

                double yOffset = RandomUtil.randDoubleInclusive(-LARGE_Y_RANGE, LARGE_Y_RANGE, random);

                if(tickMod(tick, largeMod * 2)){
                    DoublePoint basePos = new DoublePoint(LEFT_OUT, HEIGHT/2d + yOffset);
                    AbstractVector velocity = new PolarVector(largeSpeed, largeAngle);
                    SpawnUtil.blockFormation(basePos, new Angle(90), HEIGHT + 400, largeSpawns, (p) -> spawnLargeBullet(p, velocity, YELLOW, sliceBoard));
                }
                else{
                    DoublePoint basePos = new DoublePoint(RIGHT_OUT, HEIGHT/2d + yOffset);
                    AbstractVector velocity = new PolarVector(largeSpeed, 180 - largeAngle);
                    SpawnUtil.blockFormation(basePos, new Angle(90), HEIGHT + 400, largeSpawns, (p) -> spawnLargeBullet(p, velocity, YELLOW, sliceBoard));
                }
            }
        }

        private void spawnSharpBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      EnemyProjectileColors color,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, color, NORMAL_OUTBOUND, 10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

        private void spawnLargeBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      EnemyProjectileColors color,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, LARGE, color, -200, -10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}