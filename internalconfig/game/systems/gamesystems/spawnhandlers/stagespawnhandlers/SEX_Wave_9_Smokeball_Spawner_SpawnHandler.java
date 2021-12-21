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

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.SEX_WAVE_9_SMOKEBALL_SPIRAL;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class SEX_Wave_9_Smokeball_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = 40;
    private static final int HEALTH = 1000;
    private static final double SPEED = 3.6;

    private static final double Y1 = 95;
    private static final double Y2 = 155;

    public SEX_Wave_9_Smokeball_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(StageSpawns.SEX_WAVE_9_SMOKEBALL_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            Random random = GameUtil.getRandom(globalBoard);

            DoublePoint pos;
            Angle angle;
            if(!tickMod(tick, MOD * 2)){
                pos = new DoublePoint(LEFT_OUT, Y1);
                angle = new Angle(0);
            }
            else {
                pos = new DoublePoint(RIGHT_OUT, Y2);
                angle = new Angle(180);
            }
            spawnSmokeball(sliceBoard, random, pos, angle);
        }
    }

    private void spawnSmokeball(AbstractPublishSubscribeBoard sliceBoard, Random random, DoublePoint pos, Angle angle){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(SPEED, angle), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsSmokeball()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DEATH_SHOT_AND_DROP_SMALL_POWER)
                        .setProgram(makeProgram(random))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(Random random) {
        int preTimer = RandomUtil.randIntInclusive(0, 10,  random);
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, preTimer),
                new InstructionNode<>(SET_SPAWN, SEX_WAVE_9_SMOKEBALL_SPIRAL)
        ).compile();
    }
}