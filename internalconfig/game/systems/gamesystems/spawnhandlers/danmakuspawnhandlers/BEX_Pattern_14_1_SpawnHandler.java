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
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_14_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class BEX_Pattern_14_1_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int SINE_1_PHASE_1 = 0;
    private static final int SINE_1_PHASE_2 = -0;
    private static final int SINE_1_PHASE_3 = 0;
    private static final int SINE_1_PHASE_4 = 0;

    private static final int SINE_2_PHASE_1 = 1;
    private static final int SINE_2_PHASE_2 = 0;
    private static final int SINE_2_PHASE_3 = 3;
    private static final int SINE_2_PHASE_4 = 8;

    private static final double TICK_DIVISOR = 601d;

    private static final int MOD = 3;

    private static final double SPEED = 3;

    private static final double Y_RANGE = 100;
    private static final double SINE_MULTI = Y_RANGE/4;

    private static final double X_BOUND = 70;

    private static final double FINAL_ANGLE_BOUND = 6;

    private static final int TURN_TIME_LOW = 1;
    private static final int TURN_TIME_HIGH = 210;
    private static final int TURN_TIME_RANGE = TURN_TIME_HIGH - TURN_TIME_LOW;

    private static final AbstractVector VELOCITY_1 = new PolarVector(SPEED, 0);
    private static final AbstractVector VELOCITY_2 = new PolarVector(SPEED, 180);

    public BEX_Pattern_14_1_SpawnHandler(SpawnBuilder spawnBuilder,
                                         AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_14_1, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)){
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            Random random = GameUtil.getRandom(globalBoard);

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            double bossY = pos.getY();

            double sineValue1 = Math.sin(3  * 2 * Math.PI * ((tick + SINE_1_PHASE_1)/TICK_DIVISOR))
                    + Math.sin(4  * 2 * Math.PI * ((tick + SINE_1_PHASE_2)/TICK_DIVISOR))
                    + Math.sin(5  * 2 * Math.PI * ((tick + SINE_1_PHASE_3)/TICK_DIVISOR))
                    + Math.sin(23  * 2 * Math.PI * ((tick + SINE_1_PHASE_4)/TICK_DIVISOR));

            double sineValue2 = Math.sin(2  * 2 * Math.PI * ((tick + SINE_2_PHASE_1)/TICK_DIVISOR))
                    + Math.sin(4  * 2 * Math.PI * ((tick + SINE_2_PHASE_2)/TICK_DIVISOR))
                    + Math.sin(7  * 2 * Math.PI * ((tick + SINE_2_PHASE_3)/TICK_DIVISOR))
                    + Math.sin(27  * 2 * Math.PI * ((tick + SINE_2_PHASE_4)/TICK_DIVISOR));

            double y1 = bossY + sineValue1 * SINE_MULTI;
            double y2 = bossY + sineValue2 * SINE_MULTI;

            DoublePoint pos1 = new DoublePoint(LEFT_OUT, y1);
            DoublePoint pos2 = new DoublePoint(RIGHT_OUT, y2);

            Angle finalAngle1 = new Angle(-90 + RandomUtil.randDoubleInclusive(-FINAL_ANGLE_BOUND, FINAL_ANGLE_BOUND, random));
            Angle finalAngle2 = new Angle(-90 + RandomUtil.randDoubleInclusive(-FINAL_ANGLE_BOUND, FINAL_ANGLE_BOUND, random));

            int turnTime1 = (int)(TURN_TIME_LOW + (-((sineValue1 - 4)/8d) * ((double)TURN_TIME_RANGE)));
            int turnTime2 = (int)(TURN_TIME_LOW + (-((sineValue2 - 4)/8d) * ((double)TURN_TIME_RANGE)));

            spawnBullet(pos1,
                    VELOCITY_1,
                    makeProgram(new InstructionNode<>(BOUNDARY_X_HIGH, (WIDTH/2d) - X_BOUND), finalAngle1, turnTime1),
                    sliceBoard
            );

            spawnBullet(pos2,
                    VELOCITY_2,
                    makeProgram(new InstructionNode<>(BOUNDARY_X_LOW, (WIDTH/2d) + X_BOUND), finalAngle2, turnTime2),
                    sliceBoard
            );
        }
    }

    private InstructionNode<?, ?>[] makeProgram(InstructionNode<Double, Void> boundaryNode, Angle finalAngle, int turnTime){
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE,
                boundaryNode,
                new InstructionNode<>(TURN_TO, new Tuple2<>(finalAngle, turnTime))
        ).compile();
    }

    private void spawnBullet(DoublePoint pos,
                             AbstractVector velocity,
                             InstructionNode<?, ?>[] program,
                             AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, BLUE, ENEMY_OUTBOUND, 10)
                        .setProgram(program)
                        .packageAsMessage()
        );
    }
}