package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.SEX_WAVE_5_FAIRY_RING;
import static internalconfig.game.components.spawns.StageSpawns.SEX_WAVE_5_FAIRY_SPAWNER;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class SEX_Wave_5_Fairy_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = SEX_WAVE_5_FAIRY_SPAWNER.getDuration() / 4;

    private static final int HEALTH = 800;
    private static final double INIT_SPEED = 5.2;
    private static final double FINAL_SPEED = 3.6;

    private static final int START_WAIT_TIME = 10;
    private static final int SLOW_DURATION = 25;
    private static final int WAIT_DURATION = 1;
    private static final int WAIT_AFTER_DURATION = 110;
    private static final int SPEED_DURATION = 60;

    private static final double BLOCK_FORMATION_BOUND = 40;

    private static final InstructionNode<?, ?>[] PROGRAM = ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
            START_WAIT_TIME,
            SLOW_DURATION,
            WAIT_DURATION,
            SEX_WAVE_5_FAIRY_RING,
            WAIT_AFTER_DURATION,
            new PolarVector(FINAL_SPEED, new Angle(90)),
            SPEED_DURATION
    ).compile();

    public SEX_Wave_5_Fairy_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(SEX_WAVE_5_FAIRY_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            SpawnUtil.staggeredBlockFormation(
                    tick,
                    SEX_WAVE_5_FAIRY_SPAWNER.getDuration(),
                    MOD,
                    new DoublePoint(WIDTH - BLOCK_FORMATION_BOUND, TOP_OUT),
                    new DoublePoint(BLOCK_FORMATION_BOUND, TOP_OUT),
                    (pos) -> spawnFairy(sliceBoard, pos)
            );
        }
    }

    private void spawnFairy(AbstractPublishSubscribeBoard sliceBoard, DoublePoint pos){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(INIT_SPEED, -90), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsFairyYellow()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_LARGE_POWER)
                        .setProgram(PROGRAM)
                        .packageAsMessage()
        );
    }
}