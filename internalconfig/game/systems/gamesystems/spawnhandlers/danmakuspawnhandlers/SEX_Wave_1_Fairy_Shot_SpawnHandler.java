package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.spawns.DanmakuSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class SEX_Wave_1_Fairy_Shot_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final double SPEED = 5.5;

    public SEX_Wave_1_Fairy_Shot_SpawnHandler(SpawnBuilder spawnBuilder,
                                                              AbstractComponentTypeContainer componentTypeContainer) {
        super(DanmakuSpawns.SEX_WAVE_1_FAIRY_SHOT, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if(tick == 1) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

            AbstractVector velocity = new PolarVector(SPEED, angleToPlayer);

            spawnBullet(pos, velocity, sliceBoard);
        }
    }

    private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, VIOLET, NORMAL_OUTBOUND, 10)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}
