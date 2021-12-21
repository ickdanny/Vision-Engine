package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.spawns.DeathSpawns;
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
import static internalconfig.game.components.spawns.DanmakuSpawns.SEX_WAVE_1_FAIRY_SHOT;
import static internalconfig.game.components.spawns.StageSpawns.SEX_WAVE_15_FAIRY_SPAWNER;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class SEX_Wave_15_Fairy_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 15;
    private static final int HEALTH = 200;
    private static final double SPEED = 4.2;

    private static final double Y = (HEIGHT/2d) - 99;

    private static final int MAX_SPAWN_DELAY = SEX_WAVE_1_FAIRY_SHOT.getDuration();
    private static final int WAIT_TIME = 60;
    private static final int TURN_DURATION = 60;

    public SEX_Wave_15_Fairy_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(SEX_WAVE_15_FAIRY_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            spawnFairy(sliceBoard, random, new DoublePoint(RIGHT_OUT, Y));
        }
    }

    private void spawnFairy(AbstractPublishSubscribeBoard sliceBoard,
                            Random random,
                            DoublePoint pos){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(SPEED, 180), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsFairyYellow()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER)
                        .setProgram(makeProgram(random, new Angle(90)))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(Random random, Angle finalAngle) {
        int spawnDelay = RandomUtil.randIntInclusive(0, MAX_SPAWN_DELAY, random);
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, spawnDelay),
                new InstructionNode<>(SET_SPAWN, SEX_WAVE_1_FAIRY_SHOT)
        ).linkInject(
                ProgramBuilder.linearLink(
                        new InstructionNode<>(TIMER, WAIT_TIME),
                        new InstructionNode<>(TURN_TO, new Tuple2<>(finalAngle, TURN_DURATION))
                )
        ).compile();
    }
}