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
import static internalconfig.game.components.spawns.DanmakuSpawns.S2_WAVE_3_ROBO_RINGS;
import static internalconfig.game.components.spawns.StageSpawns.S2_WAVE_3_ROBO_SPAWNER;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class S2_Wave_3_Robo_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int MOD = S2_WAVE_3_ROBO_SPAWNER.getDuration() / 4;

    private static final int HEALTH = 5000;
    private static final double SPEED = 3.2;

    private static final int START_WAIT_TIME = 20;
    private static final int SLOW_DURATION = 35;
    private static final int WAIT_DURATION = 30;
    private static final int WAIT_AFTER_DURATION = 60;
    private static final int SPEED_DURATION = 60;

    private static final double BLOCK_FORMATION_BOUND = 130;
    private static final double BLOCK_FORMATION_OFFSET = 70;

    private static final InstructionNode<?, ?>[] PROGRAM = ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
            START_WAIT_TIME,
            SLOW_DURATION,
            WAIT_DURATION,
            S2_WAVE_3_ROBO_RINGS,
            WAIT_AFTER_DURATION,
            new PolarVector(SPEED, new Angle(-90)),
            SPEED_DURATION
    ).compile();

    public S2_Wave_3_Robo_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(S2_WAVE_3_ROBO_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            SpawnUtil.staggeredBlockFormation(
                    tick,
                    S2_WAVE_3_ROBO_SPAWNER.getDuration(),
                    MOD,
                    new DoublePoint(BLOCK_FORMATION_BOUND, TOP_OUT),
                    new DoublePoint(WIDTH - BLOCK_FORMATION_BOUND, TOP_OUT),
                    (pos) -> {
                        double x = pos.getX();
                        if (x < WIDTH / 2d) {
                            pos.setX(x - BLOCK_FORMATION_OFFSET);
                        } else {
                            pos.setX(x + BLOCK_FORMATION_OFFSET);
                        }
                        spawnRobo(sliceBoard, pos);
                    }
            );
        }
    }

    private void spawnRobo(AbstractPublishSubscribeBoard sliceBoard, DoublePoint pos){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(SPEED, -90), LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsRobo()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER)
                        .setProgram(PROGRAM)
                        .packageAsMessage()
        );
    }
}
