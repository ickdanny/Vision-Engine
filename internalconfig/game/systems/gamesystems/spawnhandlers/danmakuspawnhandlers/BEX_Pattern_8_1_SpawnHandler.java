package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.concurrent.atomic.AtomicBoolean;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_8_1;

public class BEX_Pattern_8_1_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 3;
    private static final int SHARP_OVERMOD = 10;

    private static final double SINE_TICK_MULTI = 1d / 300;

    private static final double SPEED = 3;
    private static final double AIM_SPEED = 2;

    private static final AbstractVector VELOCITY = new PolarVector(SPEED, -90);

    private static final double X_RADIUS = 140;

    private static final double CENTER_X_LOW = 150;
    private static final double CENTER_X_HIGH = WIDTH - CENTER_X_LOW;
    private static final double CENTER_X_AVERAGE = WIDTH/2d;
    private static final double CENTER_X_BOUND = CENTER_X_HIGH - CENTER_X_AVERAGE;

    private static final InstructionNode<?, ?>[] SHARP_PROGRAM = ProgramBuilder.linearLink(
            new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
            SET_COLLIDABLE,
            new InstructionNode<>(BOUNDARY_Y_HIGH, HEIGHT + 10d),
            new InstructionNode<>(SET_VELOCITY_TO_PLAYER, AIM_SPEED)
    ).compile();

    private static final int SIDE_MOD = 100;

    private static final int SIDE_SPAWNS = 12; //EVEN

    private static final double SIDE_SPEED = 1.4;

    public BEX_Pattern_8_1_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(BEX_PATTERN_8_1, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if(tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            double centerX = CENTER_X_AVERAGE + (Math.sin(tick * SINE_TICK_MULTI * 2 * Math.PI) * CENTER_X_BOUND);
            double x1 = centerX - X_RADIUS;
            double x2 = centerX + X_RADIUS;

            DoublePoint pos1 = new DoublePoint(x1, TOP_OUT);
            DoublePoint pos2 = new DoublePoint(x2, TOP_OUT);

            if(tickMod(tick, MOD * SHARP_OVERMOD)){
                spawnSharpBullet(pos1, sliceBoard);
                spawnSharpBullet(pos2, sliceBoard);
            } else {
                spawnSmallBullet(pos1, sliceBoard);
                spawnSmallBullet(pos2, sliceBoard);
            }
        }
        if(tickMod(tick, SIDE_MOD)){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            //noinspection SuspiciousNameCombination
            SpawnUtil.fullBlockFormation(new DoublePoint(LEFT_OUT, HEIGHT/2d), new Angle(90), HEIGHT, SIDE_SPAWNS, (pos) -> {
                if(atomicBoolean.get()){
                    spawnSideBullet(pos, new PolarVector(SIDE_SPEED, 0), sliceBoard);
                }else{
                    spawnSideBullet(new DoublePoint(RIGHT_OUT, pos.getY()), new PolarVector(SIDE_SPEED, 180), sliceBoard);
                }
                atomicBoolean.set(!atomicBoolean.get());
            });
        }
    }

    private void spawnSmallBullet(DoublePoint pos,
                                  AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, VELOCITY, SMALL, SPRING, ENEMY_OUTBOUND, 9)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }

    private void spawnSharpBullet(DoublePoint pos,
                                  AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, VELOCITY, SHARP, AZURE, ENEMY_OUTBOUND, 10)
                        .setProgram(SHARP_PROGRAM)
                        .packageAsMessage()
        );
    }

    private void spawnSideBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 AbstractPublishSubscribeBoard sliceBoard){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, BLUE, ENEMY_OUTBOUND, 0)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}