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
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_12_3;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_12_3_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int SPIRAL_FORMATION_MAX_TICK = 13;

    private static final double ANGULAR_VELOCITY = -360d/BEX_PATTERN_12_3.getDuration();

    private static final double SPEED = 1.6;

    public BEX_Pattern_12_3_SpawnHandler(SpawnBuilder spawnBuilder,
                                         AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_12_3, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

        Random pseudoRandom = GameUtil.getPseudoRandomBasedOnPosition(globalBoard, pos);

        double baseAngle = pseudoRandom.nextDouble() * 360;

        SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle, ANGULAR_VELOCITY, (angle) -> {
            AbstractVector velocity = new PolarVector(SPEED, angle);
            DoublePoint spawnPos = new PolarVector(10, angle).add(pos);
            spawnBullet(spawnPos, velocity, sliceBoard);
        });
    }

    private void spawnBullet(DoublePoint pos,
                             AbstractVector velocity,
                             AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, ROSE, NORMAL_OUTBOUND, 1)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}