package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.spawns.DanmakuSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionVelocitySpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S4_Wave_1_Flame_Arc_Spawner_Pattern_2_SpawnHandler extends AbstractPositionVelocitySpawnHandler {

    private static final double ANGLE_OFFSET = 23;
    private static final double SPEED = 3.984;

    public S4_Wave_1_Flame_Arc_Spawner_Pattern_2_SpawnHandler(SpawnBuilder spawnBuilder,
                                                              AbstractComponentTypeContainer componentTypeContainer) {
        super(DanmakuSpawns.S4_WAVE_1_FLAME_ARC_SPAWNER_PATTERN_2, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
        AbstractVector spawnerVelocity = GameUtil.getVelocity(dataStorage, entityID, velocityComponentType);
        Angle spawnerAngle = spawnerVelocity.getAngle();

        spawnBullet(pos, new PolarVector(SPEED, spawnerAngle.add(ANGLE_OFFSET)), sliceBoard);
        spawnBullet(pos, new PolarVector(SPEED, spawnerAngle.subtract(ANGLE_OFFSET)), sliceBoard);
    }

    private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, YELLOW, NORMAL_OUTBOUND, 4)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}
