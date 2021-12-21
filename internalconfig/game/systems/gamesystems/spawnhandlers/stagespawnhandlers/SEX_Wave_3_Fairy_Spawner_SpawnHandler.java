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
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.ENEMY_OUTBOUND;
import static internalconfig.game.GameConfig.SMALL_ENEMY_HITBOX;
import static internalconfig.game.GameConfig.WIDTH;
import static internalconfig.game.components.Instructions.SET_SPAWN;
import static internalconfig.game.components.Instructions.TIMER;
import static internalconfig.game.components.Instructions.TURN_TO;
import static internalconfig.game.components.spawns.DanmakuSpawns.SEX_WAVE_1_FAIRY_SHOT;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.TOP_OUT;

public class SEX_Wave_3_Fairy_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 15;
    private static final int HEALTH = 200;
    private static final double SPEED = 4.2;

    private static final double X = (WIDTH/2d);

    private static final int MAX_SPAWN_DELAY = SEX_WAVE_1_FAIRY_SHOT.getDuration();
    private static final int WAIT_TIME = 20;
    private static final int TURN_DURATION = 50;

    public SEX_Wave_3_Fairy_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.SEX_WAVE_3_FAIRY_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            Angle finalAngle = new Angle(tickMod(tick, MOD * 2) ? 0 : 180);
            spawnFairy(sliceBoard, random, new DoublePoint(X, TOP_OUT), finalAngle);
        }
    }

    private void spawnFairy(AbstractPublishSubscribeBoard sliceBoard,
                            Random random,
                            DoublePoint pos,
                            Angle finalAngle){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(SPEED, -90), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsFairyYellow()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER)
                        .setProgram(makeProgram(random, finalAngle))
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