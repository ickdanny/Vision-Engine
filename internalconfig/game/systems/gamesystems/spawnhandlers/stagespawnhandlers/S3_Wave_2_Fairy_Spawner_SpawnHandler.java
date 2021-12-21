package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.components.spawns.DanmakuSpawns;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.components.spawns.StageSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class S3_Wave_2_Fairy_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 10;
    private static final int HEALTH = 1500;

    private static final double MIN_SPEED = 1.3;
    private static final double MAX_SPEED = 4;
    private static final double SPEED_DIFF = MAX_SPEED - MIN_SPEED;
    private static final double MIN_X = 30;
    private static final double MAX_X = WIDTH * .35;
    private static final double X_DIFF = MAX_X - MIN_X;

    private static final double FINAL_ANGLE_LOW = -48;
    private static final double FINAL_ANGLE_HIGH = -42;
    private static final double FINAL_SPEED = 3.2;

    private static final int PRE_SLOW_TIMER = 30;
    private static final int SLOW_DURATION = 40;
    private static final int PRE_TIMER = 10;
    private static final int POST_TIMER_LOW = 30;
    private static final int POST_TIMER_HIGH = 40;
    private static final int SPEED_DURATION = 32;

    public S3_Wave_2_Fairy_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.S3_WAVE_2_FAIRY_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            double x = getX(random);
            double speed = getSpeed(tick);
            DoublePoint pos = new DoublePoint(x, TOP_OUT);
            AbstractVector velocity = new PolarVector(speed, -90);

            spawnFairy(sliceBoard, random, pos, velocity);
        }
    }

    private double getX(Random random){
        return MIN_X + RandomUtil.randDoubleInclusive(0, Math.nextDown(X_DIFF), random);
    }

    private double getSpeed(int tick){
        return MIN_SPEED + ((((double)tick) * SPEED_DIFF)/StageSpawns.S3_WAVE_2_FAIRY_SPAWNER.getDuration());
    }

    private void spawnFairy(AbstractPublishSubscribeBoard sliceBoard, Random random, DoublePoint pos, AbstractVector velocity){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, velocity, SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsFairyRed()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER_HALF)
                        .setProgram(makeProgram(random))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(Random random) {
        return ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
                PRE_SLOW_TIMER,
                SLOW_DURATION,
                PRE_TIMER,
                DanmakuSpawns.S3_WAVE_2_FAIRY_RINGS,
                RandomUtil.randIntInclusive(POST_TIMER_LOW, POST_TIMER_HIGH, random),
                new PolarVector(FINAL_SPEED, RandomUtil.randDoubleInclusive(FINAL_ANGLE_LOW, FINAL_ANGLE_HIGH, random)),
                SPEED_DURATION
        ).compile();
    }
}
