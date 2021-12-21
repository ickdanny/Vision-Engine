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
import static internalconfig.game.components.spawns.DanmakuSpawns.SEX_WAVE_6_FAIRY_SHOT;
import static internalconfig.game.components.spawns.StageSpawns.SEX_WAVE_6_FAIRY_SPAWNER;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class SEX_Wave_6_Fairy_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 10;
    private static final int HEALTH = 400;
    private static final double SPEED = 4.2;

    private static final double X = 90;

    private static final int WAIT_TIME = 40;
    private static final int MAX_SPAWN_DELAY = Math.min(SEX_WAVE_6_FAIRY_SHOT.getDuration(), WAIT_TIME - 1);
    private static final int TURN_DURATION = 80;

    public SEX_Wave_6_Fairy_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(SEX_WAVE_6_FAIRY_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            spawnSmokeball(sliceBoard, random, new DoublePoint(X, TOP_OUT), new Angle(89d));
            spawnSmokeball(sliceBoard, random, new DoublePoint(WIDTH - X, TOP_OUT), new Angle(91d));
        }
    }

    private void spawnSmokeball(AbstractPublishSubscribeBoard sliceBoard,
                                Random random,
                                DoublePoint pos,
                                Angle finalAngle){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(SPEED, -90), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsFairyYellow()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER_THIRD)
                        .setProgram(makeProgram(random, finalAngle))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(Random random, Angle finalAngle) {
        int spawnDelay = RandomUtil.randIntInclusive(0, MAX_SPAWN_DELAY, random);
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, spawnDelay),
                new InstructionNode<>(SET_SPAWN, SEX_WAVE_6_FAIRY_SHOT)
        ).linkInject(
                ProgramBuilder.linearLink(
                        new InstructionNode<>(TIMER, WAIT_TIME),
                        new InstructionNode<>(TURN_TO, new Tuple2<>(finalAngle, TURN_DURATION))
                )
        ).compile();
    }
}