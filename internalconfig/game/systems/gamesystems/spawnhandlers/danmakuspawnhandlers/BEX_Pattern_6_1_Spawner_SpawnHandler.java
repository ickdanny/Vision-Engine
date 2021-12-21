package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
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
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_6_1_SPAWNER;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_6_1_SPAWNER_PATTERN_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_6_1_Spawner_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int MOD = 1;

    private static final double INIT_SPEED_LOW = .001;
    private static final double INIT_SPEED_HIGH = 15;

    private static final int SLOW_TIME_LOW = 39;
    private static final int SLOW_TIME_HIGH = 149;

    private static final double INIT_ANGLE_RANGE = 30;
    private static final double FINAL_ANGLE_RANGE = 10;

    private static final double INIT_ANGLE_UP = 30;


    public BEX_Pattern_6_1_Spawner_SpawnHandler(SpawnBuilder spawnBuilder,
                                                 AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_6_1_SPAWNER, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            Random random = GameUtil.getRandom(globalBoard);

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            double speed1 = RandomUtil.randDoubleInclusive(INIT_SPEED_LOW, INIT_SPEED_HIGH, random);
            Angle angle1 = new Angle(180 - INIT_ANGLE_UP + RandomUtil.randDoubleInclusive(-INIT_ANGLE_RANGE, INIT_ANGLE_RANGE, random));
            int slowTime1 = RandomUtil.randIntInclusive(SLOW_TIME_LOW, SLOW_TIME_HIGH, random);
            Angle finalAngle1 = new Angle(-90 + RandomUtil.randDoubleInclusive(-FINAL_ANGLE_RANGE, FINAL_ANGLE_RANGE, random));
            AbstractVector initVelocity1 = new PolarVector(speed1, angle1);

            spawnSpawner(pos, initVelocity1, finalAngle1, slowTime1, sliceBoard);

            double speed2 = RandomUtil.randDoubleInclusive(INIT_SPEED_LOW, INIT_SPEED_HIGH, random);
            Angle angle2 = new Angle(INIT_ANGLE_UP + RandomUtil.randDoubleInclusive(-INIT_ANGLE_RANGE, INIT_ANGLE_RANGE, random));
            int slowTime2 = RandomUtil.randIntInclusive(SLOW_TIME_LOW, SLOW_TIME_HIGH, random);
            Angle finalAngle2 = new Angle(-90 + RandomUtil.randDoubleInclusive(-FINAL_ANGLE_RANGE, FINAL_ANGLE_RANGE, random));
            AbstractVector initVelocity2 = new PolarVector(speed2, angle2);

            spawnSpawner(pos, initVelocity2, finalAngle2, slowTime2, sliceBoard);
        }
    }

    private void spawnSpawner(DoublePoint pos,
                              AbstractVector initVelocity,
                              Angle finalAngle,
                              int slowTime,
                              AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, initVelocity, SHARP, VIOLET, NORMAL_OUTBOUND, 10)
                        .setProgram(makeProgram(finalAngle, slowTime))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(Angle finalAngle, int slowTime) {
        AbstractVector slowVelocity = new PolarVector(0, finalAngle);
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE,
                new InstructionNode<>(SLOW_DOWN_AND_TURN_TO_VELOCITY, new Tuple2<>(slowVelocity, slowTime)),
                REMOVE_VISIBLE,
                REMOVE_COLLIDABLE,
                new InstructionNode<>(SET_SPAWN, BEX_PATTERN_6_1_SPAWNER_PATTERN_1),
                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                REMOVE_ENTITY
        ).compile();
    }
}