package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionVelocitySpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_6_1_SPAWNER_PATTERN_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_6_1_Spawner_Pattern_1_SpawnHandler extends AbstractPositionVelocitySpawnHandler {

    private static final int ROWS = 5;

    private static final double SPEED_LOW = 5.7;
    private static final double SPEED_HIGH = 6.5;

    public BEX_Pattern_6_1_Spawner_Pattern_1_SpawnHandler(SpawnBuilder spawnBuilder,
                                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_6_1_SPAWNER_PATTERN_1, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
        AbstractVector spawnerVelocity = GameUtil.getVelocity(dataStorage, entityID, velocityComponentType);

        Angle spawnerAngle = spawnerVelocity.getAngle();

        SpawnUtil.columnFormation(SPEED_LOW, SPEED_HIGH, ROWS, (speed) -> {
            AbstractVector velocity = new PolarVector(speed, spawnerAngle);
            DoublePoint basePos = new PolarVector(10 + speed, spawnerAngle).add(pos);
            spawnBullet(basePos, velocity, sliceBoard);
        });

        Angle backAngle = spawnerAngle.add(180);
        SpawnUtil.columnFormation(SPEED_LOW, SPEED_HIGH, ROWS, (speed) -> {
            AbstractVector velocity = new PolarVector(speed, backAngle);
            DoublePoint basePos = new PolarVector(10 + speed, backAngle).add(pos);
            spawnBullet(basePos, velocity, sliceBoard);
        });
    }

    private void spawnBullet(DoublePoint pos,
                             AbstractVector velocity,
                             AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, VIOLET, NORMAL_OUTBOUND, 10)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}