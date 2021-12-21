package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.components.spawns.DanmakuSpawns;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.components.spawns.StageSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.TOP_OUT;

public class S1_Wave_3_Bat_Wheel_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int BAT_MOD = 30;
    private static final int BAT_SPAWNS_PER_TICK = 3;

    private static final int BAT_HEALTH = 100;

    private static final double BAT_SPEED = 3.7;
    private static final double BAT_ANGLE_BOUND = 30;

    private static final int BAT_DELAY_LOW = 0;
    private static final int BAT_DELAY_HIGH = 35;

    private static final int WHEEL_MOD = 121; //odd number to mod against 2

    private static final int WHEEL_HEALTH = 1200;

    private static final double WHEEL_SPEED = 2.6;

    private static final double WHEEL_X_OFF = 80;
    private static final double WHEEL_X_1 = (WIDTH / 2d) - WHEEL_X_OFF;
    private static final double WHEEL_X_2 = (WIDTH / 2d) + WHEEL_X_OFF;

    private static final double WHEEL_ANGLE_OFF = 20;
    private static final Angle WHEEL_ANGLE_1 = new Angle(-90 - WHEEL_ANGLE_OFF);
    private static final Angle WHEEL_ANGLE_2 = new Angle(-90 + WHEEL_ANGLE_OFF);

    private static final int WHEEL_START_WAIT_TIME = 50;
    private static final int WHEEL_SLOW_DURATION = 50;
    private static final int WHEEL_WAIT_DURATION = 20;
    private static final int WHEEL_WAIT_AFTER_DURATION = 70;
    private static final int WHEEL_SPEED_DURATION = 100;

    private static final InstructionNode<?, ?>[] WHEEL_PROGRAM_1 = makeWheelProgram(WHEEL_ANGLE_1);
    private static final InstructionNode<?, ?>[] WHEEL_PROGRAM_2 = makeWheelProgram(WHEEL_ANGLE_2);

    public S1_Wave_3_Bat_Wheel_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.S1_WAVE_3_BAT_WHEEL_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        boolean spawnBat = tickMod(tick, BAT_MOD);
        boolean spawnWheel = tickMod(tick, WHEEL_MOD);
        if (spawnBat || spawnWheel) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            if (spawnBat) {
                Random random = GameUtil.getRandom(globalBoard);

                SpawnUtil.randomTopOutPositions(
                        ENEMY_SPAWN_INBOUND,
                        WIDTH - ENEMY_SPAWN_INBOUND,
                        BAT_SPAWNS_PER_TICK,
                        random,
                        (p) -> {
                            double angle = RandomUtil.randDoubleInclusive(-90 - BAT_ANGLE_BOUND, -90 + BAT_ANGLE_BOUND, random);
                            spawnBat(sliceBoard, random, p, angle);
                        }
                );
            }
            if (spawnWheel) {
                boolean side = tick % 2 == 0;
                DoublePoint pos;
                AbstractVector velocity;
                InstructionNode<?, ?>[] program;
                if (side) {
                    pos = new DoublePoint(WHEEL_X_1, TOP_OUT);
                    velocity = new PolarVector(WHEEL_SPEED, WHEEL_ANGLE_1);
                    program = WHEEL_PROGRAM_1;
                } else {
                    pos = new DoublePoint(WHEEL_X_2, TOP_OUT);
                    velocity = new PolarVector(WHEEL_SPEED, WHEEL_ANGLE_2);
                    program = WHEEL_PROGRAM_2;
                }
                spawnWheel(sliceBoard, program, pos, velocity);
            }
        }
    }

    private void spawnBat(AbstractPublishSubscribeBoard sliceBoard, Random random, DoublePoint pos, double angle){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(BAT_SPEED, angle), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(BAT_HEALTH)
                        .setAsBat()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER_SIXTH)
                        .setProgram(makeBatProgram(random))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeBatProgram(Random random) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, RandomUtil.randIntInclusive(BAT_DELAY_LOW, BAT_DELAY_HIGH, random)),
                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.S1_WAVE_3_BAT_SHOT)
        ).compile();
    }

    private void spawnWheel(AbstractPublishSubscribeBoard sliceBoard,
                            InstructionNode<?, ?>[] program,
                            DoublePoint pos,
                            AbstractVector velocity){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, velocity, LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(WHEEL_HEALTH)
                        .setAsWheel()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER)
                        .setProgram(program)
                        .packageAsMessage()
        );
    }

    private static InstructionNode<?, ?>[] makeWheelProgram(Angle initAngle) {
        return ProgramUtil.makeShootOnceAndLeaveLongTurningEnemyProgram(
                WHEEL_START_WAIT_TIME,
                WHEEL_SLOW_DURATION,
                WHEEL_WAIT_DURATION,
                DanmakuSpawns.S1_WAVE_3_WHEEL_SPIRAL,
                WHEEL_WAIT_AFTER_DURATION,
                new PolarVector(WHEEL_SPEED, new Angle(90)),
                initAngle,
                WHEEL_SPEED_DURATION
        ).compile();
    }
}
