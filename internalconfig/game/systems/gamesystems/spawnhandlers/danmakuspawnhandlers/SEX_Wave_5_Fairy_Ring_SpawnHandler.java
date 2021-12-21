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
import static internalconfig.game.components.spawns.DanmakuSpawns.SEX_WAVE_5_FAIRY_RING;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class SEX_Wave_5_Fairy_Ring_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int SYMMETRY = 40;
    private static final int ROWS = 4;

    private static final double SPEED_LOW = 3;
    private static final double SPEED_HIGH = 6;

    public SEX_Wave_5_Fairy_Ring_SpawnHandler(SpawnBuilder spawnBuilder,
                                              AbstractComponentTypeContainer componentTypeContainer) {
        super(SEX_WAVE_5_FAIRY_RING, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

        double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

        DoublePoint basePos = new PolarVector(10, angleToPlayer).add(pos);
        SpawnUtil.columnFormation(SPEED_LOW, SPEED_HIGH, ROWS, (speed) -> {
            AbstractVector baseVelocity = new PolarVector(speed, angleToPlayer);
            SpawnUtil.ringFormation(pos, basePos, baseVelocity, SYMMETRY, (p, v) -> spawnBullet(p, v, sliceBoard));
        });
    }

    private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, BLUE, NORMAL_OUTBOUND, 5)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}
