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

import static internalconfig.game.GameConfig.ENEMY_OUTBOUND;
import static internalconfig.game.GameConfig.ENEMY_SPAWN_INBOUND;
import static internalconfig.game.GameConfig.SMALL_ENEMY_HITBOX;
import static internalconfig.game.GameConfig.WIDTH;
import static internalconfig.game.components.Instructions.SET_SPAWN;
import static internalconfig.game.components.Instructions.TIMER;

public class S1_Wave_6_Bat_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 22;
    private static final int SPAWNS_PER_TICK = 3;

    private static final int HEALTH = 100;

    private static final double SPEED = 3.7;
    private static final double ANGLE_BOUND = 30;

    private static final int DELAY_LOW = 0;
    private static final int DELAY_HIGH = 35;

    public S1_Wave_6_Bat_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.S1_WAVE_6_BAT_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            SpawnUtil.randomTopOutPositions(
                    ENEMY_SPAWN_INBOUND,
                    WIDTH - ENEMY_SPAWN_INBOUND,
                    SPAWNS_PER_TICK,
                    random,
                    (p) -> {
                        double angle = RandomUtil.randDoubleInclusive(-90 - ANGLE_BOUND, -90 + ANGLE_BOUND, random);
                        spawnBat(sliceBoard, random, p, angle);
                    }
            );

        }
    }

    private void spawnBat(AbstractPublishSubscribeBoard sliceBoard,
                          Random random,
                          DoublePoint pos,
                          double angle){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(SPEED, angle), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsBat()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER_SIXTH)
                        .setProgram(makeBatProgram(random))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeBatProgram(Random random) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, RandomUtil.randIntInclusive(DELAY_LOW, DELAY_HIGH, random)),
                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.S1_WAVE_3_BAT_SHOT)
        ).compile();
    }
}
