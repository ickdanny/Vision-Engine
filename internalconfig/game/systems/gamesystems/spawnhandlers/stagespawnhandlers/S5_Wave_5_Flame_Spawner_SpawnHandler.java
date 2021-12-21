package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
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
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S5_WAVE_1_FLAME_SHOT;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class S5_Wave_5_Flame_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 10;
    private static final int HEALTH = 1500;

    private static final double INIT_SPEED_LOW = 5.5;
    private static final double INIT_SPEED_HIGH = 7.1;
    private static final double FINAL_SPEED = 3.5;

    private static final int SLOW_DURATION = 60;
    private static final int SPEED_DURATION = 60;

    private static final double Y_LOW = TOP_OUT + 1;
    private static final double Y_HIGH = 150;

    private static final InstructionNode<?, ?>[] PROGRAM = ProgramBuilder.linearLink(
            new InstructionNode<>(SET_SPAWN, S5_WAVE_1_FLAME_SHOT),
            new InstructionNode<>(SLOW_TO_HALT, SLOW_DURATION),
            new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(new PolarVector(FINAL_SPEED, 45), SPEED_DURATION))
    ).compile();


    public S5_Wave_5_Flame_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.S5_WAVE_5_FLAME_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            double y = RandomUtil.randDoubleInclusive(Y_LOW, Y_HIGH, random);
            double speed = RandomUtil.randDoubleInclusive(INIT_SPEED_LOW, INIT_SPEED_HIGH, random);

            DoublePoint pos = new DoublePoint(LEFT_OUT + 20, y);
            AbstractVector initVelocity = new PolarVector(speed, -45);

            spawnFlame(sliceBoard, pos, initVelocity);
        }
    }

    private void spawnFlame(AbstractPublishSubscribeBoard sliceBoard,
                            DoublePoint pos,
                            AbstractVector velocity) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, velocity, SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsFlame()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER_HALF)
                        .setProgram(PROGRAM)
                        .packageAsMessage()
        );
    }
}
