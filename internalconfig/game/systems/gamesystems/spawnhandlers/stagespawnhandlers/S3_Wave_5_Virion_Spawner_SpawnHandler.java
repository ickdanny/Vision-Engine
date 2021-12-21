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

public class S3_Wave_5_Virion_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 30;

    private static final int HEALTH = 5000;

    private static final double SPEED = 2;
    private static final double ANGLE_BOUND = 5;

    private static final int DELAY_LOW = 40;
    private static final int DELAY_HIGH = 80;

    public S3_Wave_5_Virion_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.S3_WAVE_5_VIRION_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            SpawnUtil.randomTopOutPosition(
                    ENEMY_SPAWN_INBOUND,
                    WIDTH - ENEMY_SPAWN_INBOUND,
                    random,
                    (p) -> {
                        double angle = RandomUtil.randDoubleInclusive(-90 - ANGLE_BOUND, -90 + ANGLE_BOUND, random);
                        spawnVirion(sliceBoard, random, p, angle);
                    }
            );

        }
    }

    private void spawnVirion(AbstractPublishSubscribeBoard sliceBoard,
                             Random random,
                             DoublePoint pos,
                             double angle){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(SPEED, angle), LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsVirion()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER_HALF)
                        .setProgram(makeProgram(random))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(Random random) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, RandomUtil.randIntInclusive(DELAY_LOW, DELAY_HIGH, random)),
                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.S3_WAVE_5_VIRION_TILTED_RING)
        ).compile();
    }
}