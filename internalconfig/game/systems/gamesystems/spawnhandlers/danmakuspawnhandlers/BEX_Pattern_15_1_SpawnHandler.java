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

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_15_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_15_1_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final double SINE_TICK_MULTI = 1d/47.56321;

    private static final int MOD = 7;

    private static final int SYMMETRY = 10;
    private static final double ANGULAR_VELOCITY = .6 * (13d/10);

    private static final double SPEED_LOW = 1.8;
    private static final double SPEED_HIGH = 5.313;
    private static final double SPEED_AVERAGE = (SPEED_HIGH + SPEED_LOW)/2;
    private static final double SPEED_BOUND = SPEED_HIGH - SPEED_AVERAGE;

    public BEX_Pattern_15_1_SpawnHandler(SpawnBuilder spawnBuilder,
                                        AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_15_1, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);


            double speed = SPEED_AVERAGE + (Math.sin(tick * SINE_TICK_MULTI * Math.PI) * SPEED_BOUND);

            SpawnUtil.spiralFormation(tick, BEX_PATTERN_15_1.getDuration(), -90, ANGULAR_VELOCITY, (angle) -> {
                AbstractVector baseVelocity = new PolarVector(speed, angle);
                DoublePoint basePos = new PolarVector(10, angle).add(pos);
                SpawnUtil.ringFormation(pos, basePos, baseVelocity, SYMMETRY, (p, v) -> spawnBullet1(p, v, AZURE, sliceBoard));
            });

            SpawnUtil.spiralFormation(tick, BEX_PATTERN_15_1.getDuration(), -90, -ANGULAR_VELOCITY, (angle) -> {
                AbstractVector baseVelocity = new PolarVector(speed, angle);
                DoublePoint basePos = new PolarVector(10, angle).add(pos);
                SpawnUtil.ringFormation(pos, basePos, baseVelocity, SYMMETRY, (p, v) -> spawnBullet1(p, v, MAGENTA, sliceBoard));
            });
        }
    }

    private void spawnBullet1(DoublePoint pos,
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