package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.components.spawns.DanmakuSpawns;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.components.spawns.StageSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;

public class S1_Wave_2_Smokeball_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int TICK_MOD = 40;
    private static final int SPAWNS_PER_TICK = 3;

    private static final int HEALTH = 200;

    private static final double SPEED = 2.4;

    private static final int START_WAIT_TIME_LOW = 10;
    private static final int START_WAIT_TIME_HIGH = 30;
    private static final int SLOW_DURATION_LOW = 60;
    private static final int SLOW_DURATION_HIGH = 90;
    private static final int WAIT_DURATION = 10;
    private static final int WAIT_AFTER_DURATION = 50;
    private static final int SPEED_DURATION_LOW = 60;
    private static final int SPEED_DURATION_HIGH = 90;
    private static final double ANGLE_BOUND = 50;

    public S1_Wave_2_Smokeball_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.S1_WAVE_2_SMOKEBALL_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, TICK_MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            SpawnUtil.randomTopOutPositions(
                    ENEMY_SPAWN_INBOUND,
                    WIDTH - ENEMY_SPAWN_INBOUND,
                    SPAWNS_PER_TICK,
                    random,
                    (p) -> spawnSmokeball(sliceBoard, random, p)
            );
        }
    }

    private void spawnSmokeball(AbstractPublishSubscribeBoard sliceBoard, Random random, DoublePoint pos){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(SPEED, -90), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsSmokeball()
                        .setProgram(makeProgram(random))
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DEATH_SHOT_AND_DROP_SMALL_POWER_FOURTH)
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(Random random){
        int preSlowTimer = RandomUtil.randIntInclusive(START_WAIT_TIME_LOW, START_WAIT_TIME_HIGH, random);
        int slowDuration = RandomUtil.randIntInclusive(SLOW_DURATION_LOW, SLOW_DURATION_HIGH, random);
        Spawns spawn = getRandomSpawn(random);
        double angle = RandomUtil.randDoubleInclusive(-90 - ANGLE_BOUND, -90 + ANGLE_BOUND, random);
        AbstractVector finalVelocity = new PolarVector(SPEED, angle);
        double initAngle = -90;
        int speedDuration = RandomUtil.randIntInclusive(SPEED_DURATION_LOW, SPEED_DURATION_HIGH, random);
        return ProgramUtil.makeShootOnceAndLeaveTurningEnemyProgram(
                preSlowTimer,
                slowDuration,
                WAIT_DURATION,
                spawn,
                WAIT_AFTER_DURATION,
                finalVelocity,
                initAngle,
                speedDuration
        ).compile();
    }

    private Spawns getRandomSpawn(Random random){
        double d = random.nextDouble();
        if(d < 1d/3){
            return DanmakuSpawns.S1_WAVE_2_SMOKEBALL_ARC;
        }
        if(d < 2d/3){
            return DanmakuSpawns.S1_WAVE_2_SMOKEBALL_RING;
        }
        return DanmakuSpawns.S1_WAVE_2_SMOKEBALL_COLUMNS;
    }
}
