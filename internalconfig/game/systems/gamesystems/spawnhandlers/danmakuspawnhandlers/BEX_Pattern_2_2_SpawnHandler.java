package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_2_2;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_2_2_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int MOD = 2;
    private static final int STRAIGHT_OVERMOD = 7;

    private static final double INIT_TURN = 220;
    private static final double FINAL_TURN = 66;
    private static final double TOTAL_TURN = FINAL_TURN - INIT_TURN;
    private static final double ANGULAR_VELOCITY = TOTAL_TURN / BEX_PATTERN_2_2.getDuration();

    private static final int ARC_SYMMETRY = 3;
    private static final double ARC_ANGLE_INCREMENT = 35.5;

    private static final double STRAIGHT_SPEED = 3.5215;
    private static final double TURN_SPEED_LOW = 1.825253;
    private static final double TURN_SPEED_HIGH = 4.7034;
    private static final double TURN_SPEED_RANGE = TURN_SPEED_HIGH - TURN_SPEED_LOW;

    private static final int TURN_ROWS = 2;
    private static final double TURN_SPEED_MULTI_HIGH = 1.2;

    private static final int WAIT_TIME = 20;
    private static final int TURN_TIME = 35;
    private static final int SPEED_TIME = 40;

    public BEX_Pattern_2_2_SpawnHandler(SpawnBuilder spawnBuilder,
                                        AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_2_2, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

            if(tickMod(tick, MOD * STRAIGHT_OVERMOD)) {
                AbstractVector baseStraightVelocity = new PolarVector(STRAIGHT_SPEED, angleToPlayer);
                DoublePoint baseStraightPos = new PolarVector(10, angleToPlayer).add(pos);

                SpawnUtil.arcFormationIncrement(pos, baseStraightPos, baseStraightVelocity, ARC_SYMMETRY, ARC_ANGLE_INCREMENT, (p, v) ->
                        spawnStraightBullet(p, v, sliceBoard)
                );
            }

            double turnBaseAngle = angleToPlayer + 180;
            double turnBaseFinalSpeed = TURN_SPEED_LOW + (TURN_SPEED_RANGE * (((double)tick))/BEX_PATTERN_2_2.getDuration());
            SpawnUtil.columnFormation(turnBaseFinalSpeed, turnBaseFinalSpeed * TURN_SPEED_MULTI_HIGH, TURN_ROWS, (turnFinalSpeed) -> {

                AbstractVector turnInitVelocity = new PolarVector(TURN_SPEED_LOW, turnBaseAngle);
                DoublePoint turnBasePos = new PolarVector(10, turnBaseAngle).add(pos);

                SpawnUtil.spiralFormation(tick, BEX_PATTERN_2_2.getDuration(), INIT_TURN, ANGULAR_VELOCITY, (turnAdd1) -> {
                    Angle turnFinalAngle1 = turnAdd1.add(turnBaseAngle);
                    AbstractVector finalVelocity = new PolarVector(turnFinalSpeed, turnFinalAngle1);
                    if(turnAdd1.getAngle() > 180) {
                        spawnLongTurningBullet(turnBasePos, turnInitVelocity, finalVelocity, sliceBoard);
                    } else{
                        spawnShortTurningBullet(turnBasePos, turnInitVelocity, finalVelocity, sliceBoard);
                    }
                });

                SpawnUtil.spiralFormation(tick, BEX_PATTERN_2_2.getDuration(), -INIT_TURN, -ANGULAR_VELOCITY, (turnAdd2) -> {
                    Angle turnFinalAngle2 = turnAdd2.add(turnBaseAngle);
                    AbstractVector finalVelocity = new PolarVector(turnFinalSpeed, turnFinalAngle2);
                    if(turnAdd2.getAngle() < 180) {
                        spawnLongTurningBullet(turnBasePos, turnInitVelocity, finalVelocity, sliceBoard);
                    } else{
                        spawnShortTurningBullet(turnBasePos, turnInitVelocity, finalVelocity, sliceBoard);
                    }
                });
            });
        }
    }

    private void spawnStraightBullet(DoublePoint pos,
                                     AbstractVector velocity,
                                     AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, LARGE, SPRING, LARGE_OUTBOUND, -10)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }

    private void spawnLongTurningBullet(DoublePoint pos,
                                        AbstractVector initVelocity,
                                        AbstractVector finalVelocity,
                                        AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, initVelocity, SHARP, CYAN, -200, 10)
                        .setProgram(makeLongTurnProgram(finalVelocity))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeLongTurnProgram(AbstractVector finalVelocity){
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE,
                new InstructionNode<>(TIMER, WAIT_TIME),
                new InstructionNode<>(TURN_TO_LONG_ANGLE, new Tuple2<>(finalVelocity.getAngle(), TURN_TIME)),
                new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(finalVelocity, SPEED_TIME))
        ).compile();
    }

    private void spawnShortTurningBullet(DoublePoint pos,
                                        AbstractVector initVelocity,
                                        AbstractVector finalVelocity,
                                        AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, initVelocity, SHARP, CYAN, -200, 10)
                        .setProgram(makeShortTurnProgram(finalVelocity))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeShortTurnProgram(AbstractVector finalVelocity){
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE,
                new InstructionNode<>(TIMER, WAIT_TIME),
                new InstructionNode<>(TURN_TO, new Tuple2<>(finalVelocity.getAngle(), TURN_TIME)),
                new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(finalVelocity, SPEED_TIME))
        ).compile();
    }
}