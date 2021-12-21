package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.SEX_WAVE_7_WHEEL_SPIRAL;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class SEX_Wave_7_Wheel_Spiral_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int MOD = 7;
    private static final double ANGULAR_VELOCITY = 1.77665;

    private static final double MEDIUM_SPEED_LOW = 1.5;
    private static final double MEDIUM_SPEED_HIGH = 4.9;
    private static final int MEDIUM_ROWS = 5;

    private static final double SHARP_SPEED_LOW = 1.4;
    private static final double SHARP_SPEED_HIGH = 3.6;
    private static final double SHARP_ANGLE_RANGE = 10;

    private static final int SHARP_SPAWNS = 6;


    public SEX_Wave_7_Wheel_Spiral_SpawnHandler(SpawnBuilder spawnBuilder,
                                                AbstractComponentTypeContainer componentTypeContainer) {
        super(SEX_WAVE_7_WHEEL_SPIRAL, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            Random random = GameUtil.getRandom(globalBoard);

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
            double baseAngle = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID).nextDouble() * 360;
            SpawnUtil.spiralFormation(tick, SEX_WAVE_7_WHEEL_SPIRAL.getDuration(), baseAngle, ANGULAR_VELOCITY, (spiralAngle) -> {
                SpawnUtil.columnFormation(MEDIUM_SPEED_LOW, MEDIUM_SPEED_HIGH, MEDIUM_ROWS, (speed) -> {
                    AbstractVector baseVelocity = new PolarVector(speed, spiralAngle);
                    DoublePoint basePos = new PolarVector(10, spiralAngle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, baseVelocity, 3, (p, v) -> spawnMediumBullet(p, v, sliceBoard));
                });

                Angle sharpBaseAngle = spiralAngle.add(360 / 6d);
                SpawnUtil.ringFormation(sharpBaseAngle, 3, (ringAngle) -> SpawnUtil.randomVelocities(SHARP_SPEED_LOW, SHARP_SPEED_HIGH, ringAngle, SHARP_ANGLE_RANGE, SHARP_SPAWNS, random, (v) -> {
                    DoublePoint basePos = new PolarVector(10, v.getAngle()).add(pos);
                    spawnSharpBullet(basePos, v, sliceBoard);
                }));
            });
        }
    }

    private void spawnMediumBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard
            sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, SPRING, NORMAL_OUTBOUND, -1)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }

    private void spawnSharpBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard
            sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, YELLOW, NORMAL_OUTBOUND, 10)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }

}