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
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_12_1_SPAWNER_PATTERN_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_12_1_Spawner_Pattern_1_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final double ANGLE_RANGE = 3.6;

    private static final double SPEED = 2.87;

    public BEX_Pattern_12_1_Spawner_Pattern_1_SpawnHandler(SpawnBuilder spawnBuilder,
                                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_12_1_SPAWNER_PATTERN_1, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tick == 1) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            Random random = GameUtil.getRandom(globalBoard);

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            SpawnUtil.randomAngle(90 - ANGLE_RANGE, 90 + ANGLE_RANGE, random, (angle) -> {
                AbstractVector velocity = new PolarVector(SPEED, angle);
                DoublePoint spawnPos = new PolarVector(10, angle).add(pos);
                spawnBullet(spawnPos, velocity, sliceBoard);
            });
        }
    }

    private void spawnBullet(DoublePoint pos,
                             AbstractVector velocity,
                             AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, AZURE, NORMAL_OUTBOUND, 0)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}