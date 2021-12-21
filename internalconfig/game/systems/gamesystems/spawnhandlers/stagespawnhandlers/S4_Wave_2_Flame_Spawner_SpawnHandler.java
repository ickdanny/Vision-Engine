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
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class S4_Wave_2_Flame_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 15;
    private static final int HEALTH = 500;
    private static final double SPEED = 4.2;

    private static final double Y = 120;
    private static final double X = (WIDTH/2d) - 40;

    private static final int MAX_SPAWN_DELAY = 12;
    private static final int TURN_DURATION = 40;

    public S4_Wave_2_Flame_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.S4_WAVE_2_FLAME_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            spawnFlame(sliceBoard, random, new DoublePoint(RIGHT_OUT, Y), 180, 37);
            spawnFlame(sliceBoard, random, new DoublePoint(X, TOP_OUT), -90, 13);
        }
    }

    private void spawnFlame(AbstractPublishSubscribeBoard sliceBoard,
                            Random random,
                            DoublePoint pos,
                            double angle,
                            int waitTime){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(SPEED, angle), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsFlame()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER_HALF)
                        .setProgram(makeProgram(random, waitTime, new Angle(angle - 90)))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(Random random, int waitTime, Angle finalAngle) {
        int spawnDelay = RandomUtil.randIntInclusive(0, MAX_SPAWN_DELAY, random);
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, spawnDelay),
                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.S4_WAVE_1_FLAME_ARC_SPAWNER)
        ).linkInject(
                ProgramBuilder.linearLink(
                        new InstructionNode<>(TIMER, waitTime),
                        new InstructionNode<>(TURN_TO, new Tuple2<>(finalAngle, TURN_DURATION))
                )
        ).compile();
    }
}
