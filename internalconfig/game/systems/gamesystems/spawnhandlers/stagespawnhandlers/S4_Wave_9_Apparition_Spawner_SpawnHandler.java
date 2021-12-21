package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.DoublePoint;
import util.math.interval.IntInterval;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.*;
import static internalconfig.game.components.spawns.DeathSpawns.*;
import static internalconfig.game.components.spawns.StageSpawns.S4_WAVE_9_APPARITION_SPAWNER;

@SuppressWarnings("SameParameterValue")
public class S4_Wave_9_Apparition_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 35;
    private static final int HEALTH = 8000;

    private static final int X_LOW = 40;
    private static final int X_HIGH = WIDTH - X_LOW;
    private static final IntInterval X_INTERVAL = IntInterval.makeInclusive(X_LOW, X_HIGH);
    private static final double X_GAUSSIAN_MULTI = 50;

    private static final int Y_LOW = 11;
    private static final int Y_HIGH = 150;
    private static final int Y_RANGE = Y_HIGH - Y_LOW;
    private static final double Y_TICK_MULTI = 1.8;

    private static final int PRE_ATTACK_TIME = 1;
    private static final int POST_TIMER = 1;
    private static final int TRANSPARENT_DURATION = 48;

    public S4_Wave_9_Apparition_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(S4_WAVE_9_APPARITION_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            Random random = GameUtil.getRandom(globalBoard);
            double x = getX(random, tick * (tick + 14));
            double y = Y_LOW + (((tick) * Y_TICK_MULTI) % Y_RANGE);
            DoublePoint pos = new DoublePoint(x, y);

            spawnApparition(sliceBoard, pos, makeProgram(S4_WAVE_9_APPARITION_BLOCKS));
        }
    }

    private InstructionNode<?, ?>[] makeProgram(Spawns spawn) {
        return ProgramUtil.makeEnemyAppearProgram(SpawnUtil.makeApparitionSpriteInstruction(), SpawnUtil.makeApparitionAnimationComponent())
                .linkAppend(
                        ProgramBuilder.linearLink(
                                new InstructionNode<>(TIMER, PRE_ATTACK_TIME),
                                new InstructionNode<>(SET_SPAWN, spawn),
                                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                new InstructionNode<>(TIMER, POST_TIMER),
                                new InstructionNode<>(SHIFT_TRANSPARENCY_OVER_PERIOD, new Tuple2<>(.5, TRANSPARENT_DURATION / 2)),
                                REMOVE_COLLIDABLE,
                                new InstructionNode<>(SHIFT_TRANSPARENCY_OVER_PERIOD, new Tuple2<>(0d, TRANSPARENT_DURATION / 2)),
                                REMOVE_ENTITY
                        )
                ).compile();
    }

    private double getX(Random random, double center) {
        return X_INTERVAL.modIntoInterval(center + (random.nextGaussian() * X_GAUSSIAN_MULTI));
    }

    private void spawnApparition(AbstractPublishSubscribeBoard sliceBoard,
                                 DoublePoint pos,
                                 InstructionNode<?, ?>[] program) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStationaryUncollidable(pos, SMALL_ENEMY_HITBOX)
                        .markAsMob(HEALTH)
                        .setAsAppearAnimation()
                        .setDeathSpawnWithCommandAndSpawnComponent(DROP_SMALL_POWER)
                        .setProgram(program)
                        .packageAsMessage()
        );
    }
}