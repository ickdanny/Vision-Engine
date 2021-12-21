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
import static internalconfig.game.components.spawns.DanmakuSpawns.SEX_WAVE_9_SMOKEBALL_SPIRAL;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class SEX_Wave_9_Smokeball_Spiral_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int MOD = 6;
    private static final double ANGULAR_VELOCITY = 2.62;

    private static final double SPEED = 2.42;

    public SEX_Wave_9_Smokeball_Spiral_SpawnHandler(SpawnBuilder spawnBuilder,
                                                    AbstractComponentTypeContainer componentTypeContainer) {
        super(SEX_WAVE_9_SMOKEBALL_SPIRAL, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
            double baseAngle = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID).nextDouble() * 360;

            SpawnUtil.spiralFormation(tick, SEX_WAVE_9_SMOKEBALL_SPIRAL.getDuration(), baseAngle, ANGULAR_VELOCITY, (spiralAngle) -> {
                AbstractVector baseVelocity = new PolarVector(SPEED, spiralAngle);
                DoublePoint basePos = new PolarVector(10, spiralAngle).add(pos);
                SpawnUtil.ringFormation(pos, basePos, baseVelocity, 4, (p, v) -> spawnSharpBullet(p, v, sliceBoard));
            });
        }
    }

    private void spawnSharpBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, CHARTREUSE, NORMAL_OUTBOUND, 10)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}