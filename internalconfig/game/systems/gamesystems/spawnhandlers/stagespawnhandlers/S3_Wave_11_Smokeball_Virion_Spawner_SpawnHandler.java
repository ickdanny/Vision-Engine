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
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class S3_Wave_11_Smokeball_Virion_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int SMOKEBALL_MOD = 20;
    private static final int SMOKEBALL_HEALTH = 200;
    private static final double SMOKEBALL_SPEED = 4.2;
    private static final double SMOKEBALL_MIN_Y_SPAWN = 40;
    private static final double SMOKEBALL_MAX_Y_SPAWN = HEIGHT * .35;
    private static final double SMOKEBALL_Y_SPAWN_DIFF = SMOKEBALL_MAX_Y_SPAWN - SMOKEBALL_MIN_Y_SPAWN;

    private static final int VIRION_MOD = 30;

    private static final int VIRION_HEALTH = 5000;

    private static final double VIRION_SPEED = 2;
    private static final double VIRION_ANGLE_BOUND = 5;

    private static final int VIRION_DELAY_LOW = 40;
    private static final int VIRION_DELAY_HIGH = 80;

    public S3_Wave_11_Smokeball_Virion_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.S3_WAVE_11_SMOKEBALL_VIRION_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, SMOKEBALL_MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            double y1 = getY(tick);
            double y2 = getY((int)(tick * 4/3d));

            spawnSmokeball(sliceBoard, random, new DoublePoint(LEFT_OUT, y1), 0);
            spawnSmokeball(sliceBoard, random, new DoublePoint(RIGHT_OUT, y2), 180);
        }
        if(tickMod(tick, VIRION_MOD)){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            SpawnUtil.randomTopOutPosition(
                    ENEMY_SPAWN_INBOUND,
                    WIDTH - ENEMY_SPAWN_INBOUND,
                    random,
                    (p) -> {
                        double angle = RandomUtil.randDoubleInclusive(-90 - VIRION_ANGLE_BOUND, -90 + VIRION_ANGLE_BOUND, random);
                        spawnVirion(sliceBoard, random, p, angle);
                    }
            );
        }
    }

    private double getY(int tick){
        return SMOKEBALL_MIN_Y_SPAWN + ((tick + 73) % SMOKEBALL_Y_SPAWN_DIFF);
    }

    private void spawnSmokeball(AbstractPublishSubscribeBoard sliceBoard, Random random, DoublePoint pos, double angle){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(SMOKEBALL_SPEED, angle), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(SMOKEBALL_HEALTH)
                        .setAsSmokeball()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DEATH_SHOT_AND_DROP_SMALL_POWER_THIRD)
                        .setProgram(makeSmokeballProgram(random))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeSmokeballProgram(Random random) {
        int preTimer = RandomUtil.randIntInclusive(0, DanmakuSpawns.S3_WAVE_1_SMOKEBALL_SPRAY.getDuration() - 1, random);
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, preTimer),
                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.S3_WAVE_1_SMOKEBALL_SPRAY)
        ).compile();
    }

    private void spawnVirion(AbstractPublishSubscribeBoard sliceBoard,
                             Random random,
                             DoublePoint pos,
                             double angle){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(VIRION_SPEED, angle), LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(VIRION_HEALTH)
                        .setAsVirion()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER_HALF)
                        .setProgram(makeVirionProgram(random))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeVirionProgram(Random random) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, RandomUtil.randIntInclusive(VIRION_DELAY_LOW, VIRION_DELAY_HIGH, random)),
                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.S3_WAVE_5_VIRION_TILTED_RING)
        ).compile();
    }
}
