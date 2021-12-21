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
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_6_2;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_6_2_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int SYMMETRY = 74;

    private static final double SPEED_LOW = .8;
    private static final double SPEED_HIGH = .9;

    public BEX_Pattern_6_2_SpawnHandler(SpawnBuilder spawnBuilder,
                                        AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_6_2, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

        Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);

        double baseAngle = pseudoRandom.nextDouble() * 360;

        AbstractVector baseVelocity = new PolarVector(SPEED_LOW, baseAngle);
        DoublePoint basePos = new PolarVector(10, baseAngle).add(pos);
        SpawnUtil.ringFormation(pos, basePos, baseVelocity, SYMMETRY, (p, v) -> spawnBullet(p, v, sliceBoard));

        double offsetAngle = baseAngle + GeometryUtil.fullAngleDivide(SYMMETRY * 2);
        baseVelocity = new PolarVector(SPEED_HIGH, offsetAngle);
        basePos = new PolarVector(10, offsetAngle).add(pos);
        SpawnUtil.ringFormation(pos, basePos, baseVelocity, SYMMETRY, (p, v) -> spawnBullet(p, v, sliceBoard));
    }

    private void spawnBullet(DoublePoint pos,
                             AbstractVector velocity,
                             AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, BLUE, NORMAL_OUTBOUND, -1)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}