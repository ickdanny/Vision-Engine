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

public class S5_Wave_7_Flame_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 14;
    private static final int HEALTH = 1000;
    private static final double SPEED = 3.6;
    private static final double Y_LOW = 5;
    private static final double Y_HIGH = HEIGHT * .25;

    public S5_Wave_7_Flame_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.S5_WAVE_7_FLAME_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            double y1 = getY(random);
            double y2 = getY(random);

            spawnFlame(sliceBoard, random, new DoublePoint(LEFT_OUT, y1), 0);
            spawnFlame(sliceBoard, random, new DoublePoint(RIGHT_OUT, y2), 180);
        }
    }

    private double getY(Random random){
        return RandomUtil.randDoubleInclusive(Y_LOW, Y_HIGH, random);
    }

    private void spawnFlame(AbstractPublishSubscribeBoard sliceBoard, Random random, DoublePoint pos, double angle){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(SPEED, angle), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsFlame()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER_HALF)
                        .setProgram(makeProgram(random))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(Random random) {
        int preTimer = RandomUtil.randIntInclusive(0, 60, random);
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, preTimer),
                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.S5_WAVE_7_FLAME_SPRAY)
        ).compile();
    }
}