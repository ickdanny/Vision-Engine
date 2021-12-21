package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.components.spawns.DanmakuSpawns;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.BOUNDARY_Y_HIGH;
import static internalconfig.game.components.Instructions.SET_SPAWN;
import static internalconfig.game.components.spawns.DanmakuSpawns.S2_WAVE_4_WHEEL_COLUMNS;
import static internalconfig.game.components.spawns.StageSpawns.S2_WAVE_4_WHEEL_SMOKEBALL_SPAWNER;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.TOP_OUT;

public class S2_Wave_4_Wheel_Smokeball_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int WHEEL_MOD = S2_WAVE_4_WHEEL_SMOKEBALL_SPAWNER.getDuration() / 6;

    private static final int WHEEL_HEALTH = 1900;
    private static final double WHEEL_SPEED = 2.8;

    private static final int WHEEL_START_WAIT_TIME = 20;
    private static final int WHEEL_SLOW_DURATION = 30;
    private static final int WHEEL_WAIT_DURATION = 0;
    private static final int WHEEL_WAIT_AFTER_DURATION = 60;
    private static final int WHEEL_SPEED_DURATION = 60;

    private static final double WHEEL_BLOCK_FORMATION_BOUND = 50;

    private static final InstructionNode<?, ?>[] WHEEL_PROGRAM = ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
            WHEEL_START_WAIT_TIME,
            WHEEL_SLOW_DURATION,
            WHEEL_WAIT_DURATION,
            S2_WAVE_4_WHEEL_COLUMNS,
            WHEEL_WAIT_AFTER_DURATION,
            new PolarVector(WHEEL_SPEED, new Angle(90)),
            WHEEL_SPEED_DURATION
    ).compile();

    private static final int SMOKEBALL_MOD = 12;
    private static final int SMOKEBALL_HEALTH = 200;
    private static final double SMOKEBALL_SPEED = 3.2;
    private static final double SMOKEBALL_ANGLE_BOUND = 10;
    private static final double SMOKEBALL_MIN_Y_SPAWN = 10;
    private static final double SMOKEBALL_MAX_Y_SPAWN = HEIGHT * .3;

    public S2_Wave_4_Wheel_Smokeball_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(S2_WAVE_4_WHEEL_SMOKEBALL_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, WHEEL_MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            SpawnUtil.staggeredBlockFormation(
                    tick,
                    S2_WAVE_4_WHEEL_SMOKEBALL_SPAWNER.getDuration(),
                    WHEEL_MOD,
                    new DoublePoint(WIDTH - WHEEL_BLOCK_FORMATION_BOUND, TOP_OUT),
                    new DoublePoint(WHEEL_BLOCK_FORMATION_BOUND, TOP_OUT),
                    (pos) -> spawnWheel(sliceBoard, pos)
            );
        }
        if(tickMod(tick, SMOKEBALL_MOD)){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            double angle = RandomUtil.randDoubleInclusive(-90 - SMOKEBALL_ANGLE_BOUND, -90 + SMOKEBALL_ANGLE_BOUND, random);

            boolean side = tickMod(tick, SMOKEBALL_MOD * 2);
            double spawnInbound = 60;
            double xLow = side ? spawnInbound : WIDTH / 2d;
            double xHigh = side ? WIDTH / 2d : WIDTH - spawnInbound;
            SpawnUtil.randomTopOutPosition(xLow, xHigh, random,
                    (pos) -> spawnSmokeball(sliceBoard, random, pos, angle)
            );
        }
    }

    private void spawnWheel(AbstractPublishSubscribeBoard sliceBoard, DoublePoint pos){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(WHEEL_SPEED, -90), LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(WHEEL_HEALTH)
                        .setAsWheel()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER)
                        .setProgram(WHEEL_PROGRAM)
                        .packageAsMessage()
        );
    }

    private void spawnSmokeball(AbstractPublishSubscribeBoard sliceBoard, Random random, DoublePoint pos, double angle){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(SMOKEBALL_SPEED, angle), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(SMOKEBALL_HEALTH)
                        .setAsSmokeball()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DEATH_SHOT_AND_DROP_SMALL_POWER_THIRD)
                        .setProgram(makeSmokeballProgram(random))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeSmokeballProgram(Random random) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(BOUNDARY_Y_HIGH, RandomUtil.randDoubleInclusive(SMOKEBALL_MIN_Y_SPAWN, SMOKEBALL_MAX_Y_SPAWN, random)),
                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.S2_WAVE_2_SMOKEBALL_RING)
        ).compile();
    }
}