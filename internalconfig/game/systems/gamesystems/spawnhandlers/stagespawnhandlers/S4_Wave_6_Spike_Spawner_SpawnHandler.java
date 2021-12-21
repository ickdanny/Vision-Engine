package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.components.spawns.StageSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.RandomUtil;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class S4_Wave_6_Spike_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 39;
    private static final double SPEED = 4.2;
    private static final double FINAL_SPEED = 2.8;
    private static final int HEALTH = 3000;

    private static final double X_LOW = 60;
    private static final double X_HIGH = WIDTH - X_LOW;

    private static final int PRE_SLOW_TIME = 10;
    private static final int SLOW_DURATION = 33;
    private static final int PRE_ATTACK_TIME = 15;
    private static final int POST_TIMER = 1;
    private static final int SPEED_DURATION = 120;

    private static final InstructionNode<?, ?>[] PROGRAM_1 = ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
            PRE_SLOW_TIME,
            SLOW_DURATION,
            PRE_ATTACK_TIME,
            S4_WAVE_6_SPIKE_SPIRAL_1,
            POST_TIMER,
            new PolarVector(FINAL_SPEED, new Angle(90)),
            SPEED_DURATION
    ).compile();

    private static final InstructionNode<?, ?>[] PROGRAM_2 = ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
            PRE_SLOW_TIME,
            SLOW_DURATION,
            PRE_ATTACK_TIME,
            S4_WAVE_6_SPIKE_SPIRAL_2,
            POST_TIMER,
            new PolarVector(FINAL_SPEED, new Angle(90)),
            SPEED_DURATION
    ).compile();

    public S4_Wave_6_Spike_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.S4_WAVE_6_SPIKE_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            Random random = GameUtil.getRandom(globalBoard);
            double x = getX(random);
            DoublePoint pos = new DoublePoint(x, TOP_OUT);

            if (tickMod(tick, MOD * 5)) {
                spawnFlame(sliceBoard, pos, DeathSpawns.DROP_LARGE_POWER, PROGRAM_2);
            } else {
                spawnFlame(sliceBoard, pos, DeathSpawns.DROP_SMALL_POWER, PROGRAM_1);
            }
        }
    }

    private double getX(Random random) {
        return RandomUtil.randDoubleInclusive(X_LOW, X_HIGH, random);
    }

    private void spawnFlame(AbstractPublishSubscribeBoard sliceBoard,
                            DoublePoint pos,
                            DeathSpawns deathSpawn,
                            InstructionNode<?, ?>[] program) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(SPEED, -90), LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsSpike()
                        .setDeathSpawnWithCommandAndSpawnComponent(deathSpawn)
                        .setProgram(program)
                        .packageAsMessage()
        );
    }
}
