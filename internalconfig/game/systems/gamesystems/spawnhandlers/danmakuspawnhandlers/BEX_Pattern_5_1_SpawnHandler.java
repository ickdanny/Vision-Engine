package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_5_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_5_1_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int SPIRAL_FORMATION_MAX_TICK = 413;

    private static final double SINE_TICK_MULTI = 1/51.489591;

    private static final int MOD = 7;

    private static final int SYMMETRY = 7;
    private static final double ANGULAR_VELOCITY = .6 * (11d/7);

    private static final double SPEED_LOW = 2.1;
    private static final double SPEED_HIGH = 4.86;
    private static final double SPEED_AVERAGE = (SPEED_HIGH + SPEED_LOW)/2;
    private static final double SPEED_BOUND = SPEED_HIGH - SPEED_AVERAGE;

    private static final double SLOW_SCALAR = .6;

    public BEX_Pattern_5_1_SpawnHandler(SpawnBuilder spawnBuilder,
                                        AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_5_1, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)){
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);

            double baseAngle = pseudoRandom.nextDouble() * 360;

            double speed = SPEED_AVERAGE + (Math.sin(tick * SINE_TICK_MULTI * Math.PI) * SPEED_BOUND);

            SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle, ANGULAR_VELOCITY, (angle) -> {
                AbstractVector baseVelocity = new PolarVector(speed, angle);
                DoublePoint basePos = new PolarVector(10, angle).add(pos);
                SpawnUtil.ringFormation(pos, basePos, baseVelocity, SYMMETRY, (p, v) -> {
                    spawnBullet(p, v, AZURE, sliceBoard);
                    v.scale(SLOW_SCALAR);
                    spawnBullet(p, v, BLUE, sliceBoard);
                });
            });
        }
    }

    private void spawnBullet(DoublePoint pos,
                             AbstractVector velocity,
                             EnemyProjectileColors color,
                             AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, color, NORMAL_OUTBOUND, -1)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}