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
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_14_2;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_14_2_SpawnHandler extends AbstractPositionSpawnHandler {


    private static final int ARC_SYMMETRY = 71;
    private static final double ARC_TOTAL_ANGLE = 340;

    private static final double ARC_SPEED = 3.5;
    private static final double SLOW_SPEED = 1.7;

    public BEX_Pattern_14_2_SpawnHandler(SpawnBuilder spawnBuilder,
                                         AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_14_2, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tick == 1){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

                double baseAngle = angleToPlayer + 180;

                AbstractVector baseVelocity = new PolarVector(ARC_SPEED, baseAngle);
                DoublePoint basePos = new PolarVector(20, baseAngle).add(pos);

                SpawnUtil.arcFormation(pos, basePos, baseVelocity, ARC_SYMMETRY, ARC_TOTAL_ANGLE, (p, v) -> spawnBullet(p, v, sliceBoard));
                baseVelocity = new PolarVector(SLOW_SPEED, angleToPlayer);
                basePos = new PolarVector(10, angleToPlayer).add(pos);

                spawnBullet(basePos, baseVelocity, sliceBoard);
        }
    }


    private void spawnBullet(DoublePoint pos,
                             AbstractVector velocity,
                             AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, ROSE, NORMAL_OUTBOUND, 0)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}