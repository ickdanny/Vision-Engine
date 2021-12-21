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

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_4_2;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_4_2_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int MOD = 25;

    private static final int RING_SYMMETRY = 47;

    private static final double RING_SPEED = 1.9215;

    private static final int ARC_ROWS = 3;
    private static final int ARC_SYMMETRY = 3;
    private static final double ARC_TOTAL_ANGLE = 12.5;

    private static final double ARC_SPEED_LOW = 2.2;
    private static final double ARC_SPEED_HIGH = 3.4;

    public BEX_Pattern_4_2_SpawnHandler(SpawnBuilder spawnBuilder,
                                        AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_4_2, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

            AbstractVector baseRingVelocity = new PolarVector(RING_SPEED, angleToPlayer);
            DoublePoint basePos = new PolarVector(10, angleToPlayer).add(pos);

            SpawnUtil.ringFormation(pos, basePos, baseRingVelocity, RING_SYMMETRY, (p, v) ->
                    spawnRingBullet(p, v, sliceBoard)
            );

            SpawnUtil.columnFormation(ARC_SPEED_LOW, ARC_SPEED_HIGH, ARC_ROWS, (speed) -> {
                AbstractVector baseColumnVelocity = new PolarVector(speed, angleToPlayer);
                SpawnUtil.arcFormation(pos, basePos, baseColumnVelocity, ARC_SYMMETRY, ARC_TOTAL_ANGLE, (p, v) ->
                        spawnArcBullet(p, v, sliceBoard)
                );
            });

        }
    }

    private void spawnRingBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, RED, NORMAL_OUTBOUND, 0)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }

    private void spawnArcBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, RED, NORMAL_OUTBOUND, 10)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}