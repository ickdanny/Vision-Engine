package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.spawns.DanmakuSpawns;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.components.spawns.StageSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.RandomUtil;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class S3_Wave_1_Smokeball_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 20;
    private static final int HEALTH = 200;
    private static final double SPEED = 4.2;
    private static final double MIN_Y_SPAWN = 40;
    private static final double MAX_Y_SPAWN = HEIGHT * .35;
    private static final double Y_SPAWN_DIFF = MAX_Y_SPAWN - MIN_Y_SPAWN;

    public S3_Wave_1_Smokeball_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.S3_WAVE_1_SMOKEBALL_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            double y1 = getY(tick);
            double y2 = getY((int)(tick * 4/3d));

            spawnSmokeball(sliceBoard, random, new DoublePoint(LEFT_OUT, y1), 0);
            spawnSmokeball(sliceBoard, random, new DoublePoint(RIGHT_OUT, y2), 180);
        }
    }

    private double getY(int tick){
        return MIN_Y_SPAWN + (tick % Y_SPAWN_DIFF);
    }

    private void spawnSmokeball(AbstractPublishSubscribeBoard sliceBoard, Random random, DoublePoint pos, double angle){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(SPEED, angle), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsSmokeball()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DEATH_SHOT_AND_DROP_SMALL_POWER_THIRD)
                        .setProgram(makeProgram(random))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(Random random) {
        int preTimer = RandomUtil.randIntInclusive(0, DanmakuSpawns.S3_WAVE_1_SMOKEBALL_SPRAY.getDuration() - 1, random);
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, preTimer),
                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.S3_WAVE_1_SMOKEBALL_SPRAY)
        ).compile();
    }
}
