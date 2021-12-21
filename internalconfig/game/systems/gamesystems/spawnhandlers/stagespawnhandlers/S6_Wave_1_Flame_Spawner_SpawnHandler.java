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

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class S6_Wave_1_Flame_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 8;
    private static final int HEALTH = 1200;
    private static final double SPEED = 3.6;

    private static final double Y_DIST = 50;

    private static final double Y1 = 25;
    private static final double Y2 = Y1 + Y_DIST;
    private static final double Y3 = Y2 + Y_DIST;
    private static final double Y4 = Y3 + Y_DIST;

    public S6_Wave_1_Flame_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.S6_WAVE_1_FLAME_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            DoublePoint pos;
            Angle angle;
            if(tickMod(tick, MOD * 4)){
                pos = new DoublePoint(LEFT_OUT, Y1);
                angle = new Angle(0);
            }
            else if(tickMod(tick + MOD, MOD * 4)){
                pos = new DoublePoint(RIGHT_OUT, Y2);
                angle = new Angle(180);
            }
            else if(tickMod(tick + MOD + MOD, MOD * 4)){
                pos = new DoublePoint(LEFT_OUT, Y3);
                angle = new Angle(0);
            }
            else if(tickMod(tick + MOD + MOD + MOD, MOD * 4)){
                pos = new DoublePoint(RIGHT_OUT, Y4);
                angle = new Angle(180);
            }
            else{
                throw new RuntimeException("unexpected mod situation going on");
            }

            spawnFlame(sliceBoard, random, pos, angle);
        }
    }

    private void spawnFlame(AbstractPublishSubscribeBoard sliceBoard, Random random, DoublePoint pos, Angle angle){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(SPEED, angle), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsFlame()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER_HALF)
                        .setProgram(makeProgram(random))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(Random random) {
        int preTimer = RandomUtil.randIntInclusive(0, 10,  random);
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, preTimer),
                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.S6_WAVE_1_FLAME_SHOT)
        ).compile();
    }
}