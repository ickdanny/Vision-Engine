package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.components.spawns.DanmakuSpawns;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.StageSpawns.S1_WAVE_9_WHEEL_SPAWNER;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.TOP_OUT;

public class S1_Wave_9_Wheel_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int HEALTH = 3000;

    private static final double SPEED = 2.6;

    private static final double X = 90;

    private static final int START_WAIT_TIME = 40;
    private static final int SLOW_DURATION = 40;
    private static final int WAIT_DURATION = 20;
    private static final int WAIT_AFTER_DURATION = 30;
    private static final int SPEED_DURATION = 50;

    private static final InstructionNode<?, ?>[] PROGRAM_A = makeProgram(DanmakuSpawns.S1_WAVE_9_WHEEL_SPIRAL);
    private static final InstructionNode<?, ?>[] PROGRAM_B = makeProgram(DanmakuSpawns.S1_WAVE_9_WHEEL_SPIRAL_REVERSE);

    private static InstructionNode<?, ?>[] makeProgram(Spawns spawn) {
        return ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
                START_WAIT_TIME,
                SLOW_DURATION,
                WAIT_DURATION,
                spawn,
                WAIT_AFTER_DURATION,
                new PolarVector(SPEED, new Angle(-90)),
                SPEED_DURATION
        ).compile();
    }

    public S1_Wave_9_Wheel_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(S1_WAVE_9_WHEEL_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, S1_WAVE_9_WHEEL_SPAWNER.getDuration() / 4)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            DoublePoint pos = new DoublePoint(X, TOP_OUT);
            AbstractVector velocity = new PolarVector(SPEED, -90);
            InstructionNode<?, ?>[] program = tickMod(tick, S1_WAVE_9_WHEEL_SPAWNER.getDuration() / 2)
                    ? PROGRAM_A
                    : PROGRAM_B;

            SpawnUtil.mirrorFormation(pos, WIDTH / 2d, (p) ->
                    spawnWheel(sliceBoard, program, p, velocity)
            );
        }
    }

    private void spawnWheel(AbstractPublishSubscribeBoard sliceBoard,
                            InstructionNode<?, ?>[] program,
                            DoublePoint pos,
                            AbstractVector velocity) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, velocity, LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsWheel()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER)
                        .setProgram(program)
                        .packageAsMessage()
        );
    }
}
