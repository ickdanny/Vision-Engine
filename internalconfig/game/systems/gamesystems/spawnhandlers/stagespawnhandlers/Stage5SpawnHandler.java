package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.Animation;
import internalconfig.game.components.AnimationComponent;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.components.SinusoidalSpriteVerticalOffsetComponent;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;
import util.tuple.Tuple3;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.*;
import static internalconfig.game.components.spawns.DeathSpawns.DROP_LIFE;
import static internalconfig.game.components.spawns.PickupSpawns.POWER_14;
import static internalconfig.game.components.spawns.StageSpawns.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;
import static util.math.Constants.TAU;

class Stage5SpawnHandler extends AbstractStageSpawnHandler {

    public Stage5SpawnHandler(SpawnBuilder spawnBuilder) {
        super(spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        switch (stageTick(tick)) {
            case 75:
                spawnTrackStarter(ecsInterface, "10");
                break;
            case 320:
                spawnSpawner(ecsInterface, S5_WAVE_1_FLAME_SPAWNER);
                break;
            case 60 * 9 + 5:
                spawn_S5_Wave_2_Spike(ecsInterface, 80);
                break;
            case 60 * 14 - 40:
                spawn_S5_Wave_2_Spike(ecsInterface, WIDTH - 110);
                break;
            case 60 * 18 - 17:
                spawn_S5_Wave_2_Spike(ecsInterface, WIDTH / 4d + 20);
                break;
            case 60 * 23 - 53:
                spawn_S5_Wave_2_Spike(ecsInterface, (3 * WIDTH / 4d) - 50);
                break;
            case 60 * 26 + 25:
                spawn_S5_Wave_3_Spike(ecsInterface);
                break;
            case 60 * 35 + 15:
                spawn_S5_Wave_4_Spike(ecsInterface);
                break;
            case 60 * 45 - 10:
                spawnSpawner(ecsInterface, S5_WAVE_5_FLAME_SPAWNER);
                break;
            case 60 * 48:
                spawn_S5_Wave_6_Spike(ecsInterface);
                break;
            case 60 * 53:
                spawn_S5_Midboss_Spike(ecsInterface);
                break;
            case 60 * 71 - 15:
                spawnSpawner(ecsInterface, S5_WAVE_7_FLAME_SPAWNER);
                break;
            case 60 * 95 + 75:
                spawn_S5_Boss(ecsInterface);
                break;
        }
    }

    private void spawn_S5_Wave_2_Spike(AbstractECSInterface ecsInterface, double x) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        DoublePoint pos = new DoublePoint(x, TOP_OUT);
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(3.2, -90), LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(13000)
                        .setAsSpike()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_LARGE_POWER)
                        .setProgram(S5_WAVE_2_SPIKE_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S5_WAVE_2_SPIKE_PROGRAM = ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
            25,
            30,
            1,
            S5_WAVE_2_SPIKE_RINGS,
            65,
            new PolarVector(3.2, new Angle(-90)),
            51
    ).compile();

    private static final double S5_WAVE_3_TOTAL_WIDTH = 400;
    private static final int S5_WAVE_3_SPAWNS = 7;
    private static final double S5_WAVE_3_X_OFFSET = 50;

    private static final int S5_WAVE_3_HEALTH = 11000;

    private static final double S5_WAVE_3_INIT_SPEED = 3.2;

    private static final int S5_WAVE_3_PRE_SLOW_TIME = 22;
    private static final int S5_WAVE_3_SLOW_DURATION = 24;
    private static final int S5_WAVE_3_PRE_TIME = 1;

    private static final double S5_WAVE_3_FINAL_SPEED = 1.7;
    private static final int S5_WAVE_3_TURN_TIME = (int)(60 * 10.5);

    private void spawn_S5_Wave_3_Spike(AbstractECSInterface ecsInterface) {
        SpawnUtil.blockFormation(
                new DoublePoint((WIDTH/2d) + S5_WAVE_3_X_OFFSET, TOP_OUT),
                S5_WAVE_3_TOTAL_WIDTH,
                S5_WAVE_3_SPAWNS,
                (pos) -> spawn_Wave_3_Spike(ecsInterface, pos)
        );
    }

    private void spawn_Wave_3_Spike(AbstractECSInterface ecsInterface, DoublePoint pos) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(S5_WAVE_3_INIT_SPEED, -90), LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(S5_WAVE_3_HEALTH)
                        .setAsSpike()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER)
                        .setProgram(S5_WAVE_3_SPIKE_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S5_WAVE_3_SPIKE_PROGRAM =
            ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, S5_WAVE_3_PRE_SLOW_TIME),
                    new InstructionNode<>(SLOW_TO_HALT, S5_WAVE_3_SLOW_DURATION),
                    new InstructionNode<>(TIMER, S5_WAVE_3_PRE_TIME),
                    new InstructionNode<>(SET_SPAWN, S5_WAVE_3_SPIKE_SHOT),
                    new InstructionNode<>(
                            SPEED_UP_AND_TURN_TO_VELOCITY,
                            new Tuple3<>(
                                    new PolarVector(S5_WAVE_3_FINAL_SPEED, 90 + 1),
                                    new Angle(-90),
                                    S5_WAVE_3_TURN_TIME
                            )
                    )
            ).compile();

    private void spawn_S5_Wave_4_Spike(AbstractECSInterface ecsInterface) {
        SpawnUtil.blockFormation(
                new DoublePoint((WIDTH/2d) - S5_WAVE_3_X_OFFSET, TOP_OUT),
                S5_WAVE_3_TOTAL_WIDTH,
                S5_WAVE_3_SPAWNS,
                (pos) -> spawn_Wave_4_Spike(ecsInterface, pos)
        );
    }

    private void spawn_Wave_4_Spike(AbstractECSInterface ecsInterface, DoublePoint pos) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(S5_WAVE_3_INIT_SPEED, -90), LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(S5_WAVE_3_HEALTH)
                        .setAsSpike()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER)
                        .setProgram(S5_WAVE_4_SPIKE_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S5_WAVE_4_SPIKE_PROGRAM =
            ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, S5_WAVE_3_PRE_SLOW_TIME),
                    new InstructionNode<>(SLOW_TO_HALT, S5_WAVE_3_SLOW_DURATION),
                    new InstructionNode<>(TIMER, S5_WAVE_3_PRE_TIME),
                    new InstructionNode<>(SET_SPAWN, S5_WAVE_3_SPIKE_SHOT),
                    new InstructionNode<>(
                            SPEED_UP_AND_TURN_TO_VELOCITY,
                            new Tuple3<>(
                                    new PolarVector(S5_WAVE_3_FINAL_SPEED, 90 - 1),
                                    new Angle(-90),
                                    S5_WAVE_3_TURN_TIME
                            )
                    )
            ).compile();

    private static final double S5_WAVE_6_TOTAL_WIDTH = 300;
    private static final int S5_WAVE_6_SPAWNS = 4;
    private static final double S5_WAVE_6_X_OFFSET = 120;

    private static final int S5_WAVE_6_HEALTH = 10000;

    private static final int S5_WAVE_6_PRE_SLOW_TIME = 22;
    private static final int S5_WAVE_6_SLOW_DURATION = 24;
    private static final int S5_WAVE_6_PRE_TIME = 1;

    private static final double S5_WAVE_6_FINAL_SPEED = 2.2;
    private static final int S5_WAVE_6_TURN_TIME = (int)(60 * 4.7);

    private void spawn_S5_Wave_6_Spike(AbstractECSInterface ecsInterface){
        SpawnUtil.blockFormation(
                new DoublePoint((WIDTH/2d) + S5_WAVE_6_X_OFFSET, TOP_OUT),
                S5_WAVE_6_TOTAL_WIDTH,
                S5_WAVE_6_SPAWNS,
                (pos) -> spawn_Wave_6_Spike(ecsInterface, pos, 4, S5_WAVE_6_SPIKE_PROGRAM_1)
        );
        SpawnUtil.blockFormation(
                new DoublePoint((WIDTH/2d) - S5_WAVE_6_X_OFFSET, TOP_OUT),
                S5_WAVE_6_TOTAL_WIDTH,
                S5_WAVE_6_SPAWNS,
                (pos) -> spawn_Wave_6_Spike(ecsInterface, pos, 2.7, S5_WAVE_6_SPIKE_PROGRAM_2)
        );
    }

    private void spawn_Wave_6_Spike(AbstractECSInterface ecsInterface,
                                    DoublePoint pos,
                                    double speed,
                                    InstructionNode<?, ?>[] program) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(speed, -90), LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(S5_WAVE_6_HEALTH)
                        .setAsSpike()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER)
                        .setProgram(program)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S5_WAVE_6_SPIKE_PROGRAM_1 =
            ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, S5_WAVE_6_PRE_SLOW_TIME),
                    new InstructionNode<>(SLOW_TO_HALT, S5_WAVE_6_SLOW_DURATION),
                    new InstructionNode<>(TIMER, S5_WAVE_6_PRE_TIME),
                    new InstructionNode<>(SET_SPAWN, S5_WAVE_3_SPIKE_SHOT),
                    new InstructionNode<>(
                            SPEED_UP_AND_TURN_TO_VELOCITY,
                            new Tuple3<>(
                                    new PolarVector(S5_WAVE_6_FINAL_SPEED, 90 + 1),
                                    new Angle(-90),
                                    S5_WAVE_6_TURN_TIME
                            )
                    )
            ).compile();

    private static final InstructionNode<?, ?>[] S5_WAVE_6_SPIKE_PROGRAM_2 =
            ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, S5_WAVE_6_PRE_SLOW_TIME),
                    new InstructionNode<>(SLOW_TO_HALT, S5_WAVE_6_SLOW_DURATION),
                    new InstructionNode<>(TIMER, S5_WAVE_6_PRE_TIME),
                    new InstructionNode<>(SET_SPAWN, S5_WAVE_3_SPIKE_SHOT),
                    new InstructionNode<>(
                            SPEED_UP_AND_TURN_TO_VELOCITY,
                            new Tuple3<>(
                                    new PolarVector(S5_WAVE_6_FINAL_SPEED, 90 - 1),
                                    new Angle(-90),
                                    S5_WAVE_6_TURN_TIME
                            )
                    )
            ).compile();

    private void spawn_S5_Midboss_Spike(AbstractECSInterface ecsInterface) {
        DoublePoint center = new DoublePoint(WIDTH / 2d, TOP_OUT);
        AbstractVector velocity = new PolarVector(4, -90);

        ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeStraightPathCollidable(center, velocity, LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(74000)
                        .setAsSpike()
                        .setProgram(S5_MIDBOSS_PROGRAM)
                        .setDeathSpawnWithCommandAndSpawnComponent(DROP_LIFE)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S5_MIDBOSS_PROGRAM =
            ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
                    0,
                    80,
                    0,
                    MB5_PATTERN_1_1,
                    30,
                    new PolarVector(2.5, 90),
                    160
            ).compile();

    private void spawn_S5_Boss(AbstractECSInterface ecsInterface) {
        ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeStraightPathUncollidable(
                        new DoublePoint(WIDTH / 2d, TOP_OUT),
                        new PolarVector(5, -90),
                        LARGE_ENEMY_HITBOX,
                        ENEMY_OUTBOUND
                )
                        .markAsBoss()
                        .setSpriteInstruction(new SpriteInstruction("5_idle"))
                        .setAnimation(new AnimationComponent(
                                new Animation[]{
                                        new Animation(true, "5_left"),
                                        new Animation(true, "5_idle"),
                                        new Animation(true, "5_right")
                                },
                                1
                        ))
                        .setSinusoidalSpriteVerticalOffset(new SinusoidalSpriteVerticalOffsetComponent(TAU / 180, 10))
                        .setProgram(S5_BOSS_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S5_BOSS_PROGRAM =
            ProgramUtil.makeBossEntryProgram(30, "5_pre")
                    //PHASE 1: SPIRAL
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(110000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B5_PATTERN_1_1)
                            ).linkAppend(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 2: DOUBLE BIG SPIRAL AND WALLS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(95000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B5_PATTERN_2_1)
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(55, 120, 1.55)
                            ).linkAppend(
                                    ProgramBuilder.linearLink(
                                            new InstructionNode<>(SET_SPAWN, B5_PATTERN_2_1)
                                    )
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(55, 120, 1.55)
                            ).linkAppend(
                                    ProgramBuilder.linearLink(
                                            new InstructionNode<>(SET_SPAWN, B5_PATTERN_2_2),
                                            WAIT_UNTIL_SPAWN_COMPONENT_EMPTY
                                    )
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(55, 120, 1.55)
                            ).linkBackToFront().noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_14),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0)),
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 3: HOMING SPIRAL
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(80000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B5_PATTERN_3_1)
                            ).linkAppend(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 4: RAIN
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(110000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B5_PATTERN_4_1_SPAWNER)
                            ).linkAppend(
                                    ProgramBuilder.linearLink(
                                            new InstructionNode<>(TIMER, 92),
                                            new InstructionNode<>(ADD_SPAWN, B5_PATTERN_4_2),
                                            new InstructionNode<>(TIMER, 32)
                                    ).linkAppend(
                                            ProgramUtil.makeStrictBossMoveProgramNoLoop(55, 120, 1.55)
                                    ).linkBackToFront()
                            ).noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_14),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0)),
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 5: FALLING SPIRAL
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(110000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B5_PATTERN_5_1)
                            ).linkAppend(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 6 SQUARE AND 4 WINDMILL
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(80000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B5_PATTERN_6_1)
                            ).linkAppend(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_14),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0)),
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    .linkAppend(ProgramUtil.makeTimerProgram(120))
                    //PHASE 7 FERN
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(105000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B5_PATTERN_7_1)
                            ).linkAppend(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 8 OCTAGON
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(80000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B5_PATTERN_8_1)
                            ).linkAppend(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramUtil.makeBossEndProgram()
                    )
                    .compile();
}
