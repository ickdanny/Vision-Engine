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
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_9_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_9_1_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int MOD = 7;
    private static final int SPIRAL_FORMATION_MAX_TICK = 413;

    private static final double SINE_TICK_MULTI_1 = 1 / 140.489591;
    private static final double SINE_TICK_MULTI_2 = 1d/120;

    private static final int SYMMETRY_1 = 6;
    private static final double ANGULAR_VELOCITY_1 = .6 * (7d/6);

    private static final int SYMMETRY_2 = 9;
    private static final double ANGULAR_VELOCITY_2 = .6 * 2 * (7d/9);

    private static final double SPEED_LOW_1 = .9;
    private static final double SPEED_HIGH_1 = 2.16;
    private static final double SPEED_AVERAGE_1 = (SPEED_HIGH_1 + SPEED_LOW_1) / 2;
    private static final double SPEED_BOUND_1 = SPEED_HIGH_1 - SPEED_AVERAGE_1;

    private static final double SPEED_LOW_2 = 2.03;
    private static final double SPEED_HIGH_2 = 4.46;
    private static final double SPEED_AVERAGE_2 = (SPEED_HIGH_2 + SPEED_LOW_2) / 2;
    private static final double SPEED_BOUND_2 = SPEED_HIGH_2 - SPEED_AVERAGE_2;

    public BEX_Pattern_9_1_SpawnHandler(SpawnBuilder spawnBuilder,
                                        AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_9_1, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if(tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);

            double baseAngle1 = pseudoRandom.nextDouble() * 360;

            double speed1 = SPEED_AVERAGE_1 + (Math.sin(tick * SINE_TICK_MULTI_1 * 2 * Math.PI) * SPEED_BOUND_1);

            SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle1, ANGULAR_VELOCITY_1, (angle) -> {
                AbstractVector baseVelocity1 = new PolarVector(speed1, angle);
                DoublePoint basePos1 = new PolarVector(10, angle).add(pos);
                SpawnUtil.ringFormation(pos, basePos1, baseVelocity1, SYMMETRY_1, (p, v) -> spawnBullet1(p, v, sliceBoard));
            });

            double baseAngle2 = pseudoRandom.nextDouble() * 360;

            double speed2 = SPEED_AVERAGE_2 + (Math.sin(tick * SINE_TICK_MULTI_2 * 2 * Math.PI) * SPEED_BOUND_2);

            SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle2, ANGULAR_VELOCITY_2, (angle) -> {
                AbstractVector baseVelocity2 = new PolarVector(speed2, angle);
                DoublePoint basePos2 = new PolarVector(10, angle).add(pos);
                SpawnUtil.ringFormation(pos, basePos2, baseVelocity2, SYMMETRY_2, (p, v) -> spawnBullet2(p, v, sliceBoard));
            });
        }
    }

    private void spawnBullet1(DoublePoint pos,
                              AbstractVector velocity,
                              AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, ROSE, NORMAL_OUTBOUND, -1)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }

    private void spawnBullet2(DoublePoint pos,
                              AbstractVector velocity,
                              AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, VIOLET, NORMAL_OUTBOUND, -1)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}