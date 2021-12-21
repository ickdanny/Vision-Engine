package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramUtil;
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
import static internalconfig.game.components.spawns.DanmakuSpawns.S3_WAVE_8_FAIRY_SPRAY;
import static internalconfig.game.components.spawns.DeathSpawns.DROP_SMALL_POWER_HALF;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class S3_Wave_16_Fairy_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 15;
    private static final int HEALTH = 1000;

    private static final double MIN_SPEED = 3;
    private static final double MAX_SPEED = 4;
    private static final double SPEED_DIFF = MAX_SPEED - MIN_SPEED;
    private static final double MIN_X = 30;
    private static final double MAX_X = WIDTH - 30;
    private static final double X_DIFF = MAX_X - MIN_X;

    private static final InstructionNode<?, ?>[] PROGRAM =
            ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
                    0,
                    60,
                    0,
                    S3_WAVE_8_FAIRY_SPRAY,
                    30,
                    new PolarVector(2.5, 90),
                    160
            ).compile();

    public S3_Wave_16_Fairy_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.S3_WAVE_16_FAIRY_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            double x = getX(random);
            double speed = getSpeed(random);
            DoublePoint pos = new DoublePoint(x, TOP_OUT);
            AbstractVector velocity = new PolarVector(speed, -90);

            spawnFairy(sliceBoard, pos, velocity);
        }
    }

    private double getX(Random random){
        return MIN_X + RandomUtil.randDoubleInclusive(0, Math.nextDown(X_DIFF), random);
    }

    private double getSpeed(Random random){
        return MIN_SPEED + RandomUtil.randDoubleInclusive(0, Math.nextDown(SPEED_DIFF), random);
    }

    private void spawnFairy(AbstractPublishSubscribeBoard sliceBoard, DoublePoint pos, AbstractVector velocity){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, velocity, SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsFairyYellow()
                        .setProgram(PROGRAM)
                        .setDeathSpawnWithCommandAndSpawnComponent(DROP_SMALL_POWER_HALF)
                        .packageAsMessage()
        );
    }


}