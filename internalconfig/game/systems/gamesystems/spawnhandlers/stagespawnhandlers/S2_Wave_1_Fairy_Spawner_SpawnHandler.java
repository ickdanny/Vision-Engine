package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.spawns.DanmakuSpawns;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.StageSpawns.S2_WAVE_1_FAIRY_SPAWNER;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.TOP_OUT;

@SuppressWarnings("SameParameterValue")
public class S2_Wave_1_Fairy_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {
    private static final int MOD = S2_WAVE_1_FAIRY_SPAWNER.getDuration() / 8;

    private static final int HEALTH = 400;

    private static final double SPEED = 3.7;

    private static final double INIT_ANGLE_1 = -65;
    private static final double INIT_ANGLE_2 = 180 - INIT_ANGLE_1;

    private static final double FINAL_ANGLE_1 = 0;
    private static final double FINAL_ANGLE_2 = 180 - FINAL_ANGLE_1;

    private static final int TURN_DURATION = 120;

    private static final double FIRST_X_1 = 0;
    private static final double FIRST_X_2 = WIDTH - FIRST_X_1;

    private static final double LAST_X_1 = (WIDTH / 2d) - 60;
    private static final double LAST_X_2 = WIDTH - LAST_X_1;

    public S2_Wave_1_Fairy_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(S2_WAVE_1_FAIRY_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            SpawnUtil.staggeredBlockFormation(
                    tick,
                    S2_WAVE_1_FAIRY_SPAWNER.getDuration(),
                    MOD,
                    new DoublePoint(FIRST_X_1, TOP_OUT),
                    new DoublePoint(LAST_X_1, TOP_OUT),
                    (pos) -> spawnFairy(
                            pos,
                            new PolarVector(SPEED, INIT_ANGLE_1),
                            FINAL_ANGLE_1,
                            TURN_DURATION,
                            sliceBoard
                    )
            );

            SpawnUtil.staggeredBlockFormation(
                    tick,
                    S2_WAVE_1_FAIRY_SPAWNER.getDuration(),
                    MOD,
                    new DoublePoint(FIRST_X_2, TOP_OUT),
                    new DoublePoint(LAST_X_2, TOP_OUT),
                    (pos) -> spawnFairy(
                            pos,
                            new PolarVector(SPEED, INIT_ANGLE_2),
                            FINAL_ANGLE_2,
                            TURN_DURATION,
                            sliceBoard
                    )
            );

        }
    }

    private void spawnFairy(DoublePoint pos,
                            AbstractVector velocity,
                            double angle,
                            int turnDuration,
                            AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, velocity, SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(HEALTH)
                        .setAsFairyOrange()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER_HALF)
                        .setProgram(makeProgram(MOD, angle, turnDuration))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(int delay, double angle, int turnDuration) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, delay),
                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.S2_WAVE_1_FAIRY_SHOT)
        ).linkInject(
                ProgramBuilder.linearLink(
                        new InstructionNode<>(TURN_TO, new Tuple2<>(new Angle(angle), turnDuration))
                )
        ).compile();
    }
}
