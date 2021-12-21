package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_12_1_SPAWNER_PATTERN_2;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_12_1_Spawner_Pattern_2_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int SPAWNS = 40;

    private static final double GAUSSIAN_MULTIPLIER = 50; //larger = more spawns outwards

    private static final double ANGLE_PROXIMITY_DIVISOR = 4; //smaller = more concentrated upwards

    private static final double FINAL_SPEED_LOW = .7;
    private static final double FINAL_SPEED_HIGH = 1.199;

    private static final double INIT_SPEED_LOW = 1.2;
    private static final double INIT_SPEED_HIGH = 17;
    private static final double INIT_SPEED_HIGH_RANGE = INIT_SPEED_HIGH - INIT_SPEED_LOW;

    private static final int SLOW_TIME_LOW = 60;
    private static final int SLOW_TIME_HIGH = 120;

    public BEX_Pattern_12_1_Spawner_Pattern_2_SpawnHandler(SpawnBuilder spawnBuilder,
                                                           AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_12_1_SPAWNER_PATTERN_2, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        Random random = GameUtil.getRandom(globalBoard);

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

        for (int i = 0; i < SPAWNS; ++i) {
            Angle angle = new Angle(90 + (random.nextGaussian() * GAUSSIAN_MULTIPLIER));
            double angleProximity = Math.abs(angle.smallerDifference(90));
            double angleProximityConstant = 1d / ((angleProximity / ANGLE_PROXIMITY_DIVISOR) + 1);
            double highestInitSpeed = INIT_SPEED_LOW + (angleProximityConstant * INIT_SPEED_HIGH_RANGE);
            SpawnUtil.randomSpeed(INIT_SPEED_LOW, highestInitSpeed, random, (initSpeed) -> SpawnUtil.randomSpeed(FINAL_SPEED_LOW, FINAL_SPEED_HIGH, random, (finalSpeed) -> {
                int slowTime = RandomUtil.randIntInclusive(SLOW_TIME_LOW, SLOW_TIME_HIGH, random);
                AbstractVector initVelocity = new PolarVector(initSpeed, angle);
                DoublePoint spawnPos = new PolarVector(10 + (2 * initSpeed), angle).add(pos);
                AbstractVector finalVelocity = new PolarVector(finalSpeed, angle);

                spawnBullet(spawnPos,
                        initVelocity,
                        finalVelocity,
                        slowTime,
                        random.nextBoolean() ? MEDIUM : SMALL,
                        getColor(random),
                        sliceBoard
                );
            }));
        }
    }

    private EnemyProjectileColors getColor(Random random) {
        int i = RandomUtil.randIntInclusive(1, 3, random);
        switch (i) {
            case 1:
                return BLUE;
            case 2:
                return AZURE;
            case 3:
                return CYAN;
            default:
                throw new RuntimeException("bad index: " + i);
        }
    }


    private void spawnBullet(DoublePoint pos,
                             AbstractVector initVelocity,
                             AbstractVector finalVelocity,
                             int slowTime,
                             EnemyProjectileTypes type,
                             EnemyProjectileColors color,
                             AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, initVelocity, type, color, NORMAL_OUTBOUND, 0)
                        .setProgram(makeProgram(slowTime, finalVelocity))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(int slowTime, AbstractVector finalVelocity) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE,
                new InstructionNode<>(SLOW_DOWN_TO_VELOCITY, new Tuple2<>(finalVelocity, slowTime))
        ).compile();
    }
}
