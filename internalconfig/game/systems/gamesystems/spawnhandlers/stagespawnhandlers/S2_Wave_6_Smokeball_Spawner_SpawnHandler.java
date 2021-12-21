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
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;

public class S2_Wave_6_Smokeball_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 12;
    private static final int HEALTH = 200;
    private static final double SPEED = 3.2;
    private static final double ANGLE_BOUND = 10;
    private static final double MIN_Y_SPAWN = 10;
    private static final double MAX_Y_SPAWN = HEIGHT * .3;

    public S2_Wave_6_Smokeball_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.S2_WAVE_6_SMOKEBALL_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            double angle = RandomUtil.randDoubleInclusive(-90 - ANGLE_BOUND, -90 + ANGLE_BOUND, random);

            boolean side = tickMod(tick, MOD * 2);
            double spawnInbound = 60;
            double xLow = side ? spawnInbound : WIDTH / 2d;
            double xHigh = side ? WIDTH / 2d : WIDTH - spawnInbound;

            SpawnUtil.randomTopOutPosition(xLow, xHigh, random,
                    (pos) -> spawnSmokeball(sliceBoard, random, pos, angle)
            );
        }
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
        return ProgramBuilder.linearLink(
                new InstructionNode<>(BOUNDARY_Y_HIGH, RandomUtil.randDoubleInclusive(MIN_Y_SPAWN, MAX_Y_SPAWN, random)),
                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.S2_WAVE_2_SMOKEBALL_RING)
        ).compile();
    }
}
