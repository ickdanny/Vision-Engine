package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.SEX_WAVE_16_FAIRY_SPRAY;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class SEX_Wave_16_Fairy_Spray_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final double SPEED_LOW = 1.5;
    private static final double SPEED_HIGH = 7;

    private static final int SPAWNS = 2;

    private static final double DISTANCE = 14;
    private static final int SYMMETRY = 7;

    public SEX_Wave_16_Fairy_Spray_SpawnHandler(SpawnBuilder spawnBuilder,
                                                AbstractComponentTypeContainer componentTypeContainer) {
        super(SEX_WAVE_16_FAIRY_SPRAY, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        Random random = GameUtil.getRandom(globalBoard);

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

        for(int i = 0; i < SPAWNS; ++i) {
            double speed = RandomUtil.randDoubleInclusive(SPEED_LOW, SPEED_HIGH, random);
            double angle = random.nextDouble() * 360;
            AbstractVector velocity = new PolarVector(speed, angle);
            AbstractVector spawnPosOffset = new PolarVector(10, angle);

            EnemyProjectileColors color = SpawnUtil.randomColor(random);

            double baseSpawnAngle = random.nextDouble() * 360;
            DoublePoint basePos = new PolarVector(DISTANCE, baseSpawnAngle).add(pos);

            SpawnUtil.ringFormation(pos, basePos, SYMMETRY, (spawnPos) -> spawnBullet(spawnPosOffset.add(spawnPos), velocity, color, sliceBoard));
        }
    }

    private void spawnBullet(DoublePoint pos,
                             AbstractVector velocity,
                             EnemyProjectileColors color,
                             AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, color, NORMAL_OUTBOUND, color.ordinal())
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}
