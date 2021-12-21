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
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.*;
import static internalconfig.game.components.spawns.DeathSpawns.*;
import static internalconfig.game.components.spawns.PickupSpawns.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;
import static internalconfig.game.components.spawns.StageSpawns.*;
import static util.math.Constants.TAU;

class StageEXSpawnHandler extends AbstractStageSpawnHandler {

    public StageEXSpawnHandler(SpawnBuilder spawnBuilder) {
        super(spawnBuilder);
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        switch (stageTick(tick)) {
            case 0:
                spawnTrackStarter(ecsInterface, "14");
//                break;
//            case 60 * 4 - 10:
//                spawnSpawner(ecsInterface, SEX_WAVE_1_FAIRY_SPAWNER);
//                break;
//            case 60 * 6 + 28:
//                spawnSpawner(ecsInterface, SEX_WAVE_2_FAIRY_SPAWNER);
//                break;
//            case 60 * 9 + 6:
//                spawnSpawner(ecsInterface, SEX_WAVE_3_FAIRY_SPAWNER);
//                break;
//            case 60 * 11:
//                spawn_SEX_Wave_4_Fairy(ecsInterface, 110);
//                break;
//            case 60 * 13 + 30:
//                spawn_SEX_Wave_4_Fairy(ecsInterface, WIDTH - 110);
//                break;
//            case 60 * 18 - 10:
//                spawnSpawner(ecsInterface, SEX_WAVE_5_FAIRY_SPAWNER);
//                break;
//            case 60 * 25:
//                spawnSpawner(ecsInterface, SEX_WAVE_6_FAIRY_SPAWNER);
//                break;
//            case 60 * 44:
//                spawn_SEX_Wave_7_Wheel(ecsInterface, WIDTH / 2d, 9000);
//                break;
//            case 60 * 50 - 40:
//                spawn_SEX_Wave_7_Wheel(ecsInterface, 110, 10000);
//                break;
//            case 60 * 55 - 20:
//                spawn_SEX_Wave_7_Wheel(ecsInterface, WIDTH / 2d, 11000);
//                break;
//            case 60 * 60:
//                spawn_SEX_Wave_7_Wheel(ecsInterface, WIDTH - 110, 12000);
//                break;
//            case 60 * 67:
//                spawnSpawner(ecsInterface, SEX_WAVE_8_SMOKEBALL_SPAWNER);
//                break;
//            case 60 * 77:
//                spawnSpawner(ecsInterface, SEX_WAVE_9_SMOKEBALL_SPAWNER);
//                break;
//            case 60 * 87 + 10:
//                spawn_SEX_Midboss_Fairy(ecsInterface);
//                break;
//            case 60 * 109 - 20:
//                spawnSpawner(ecsInterface, SEX_WAVE_10_FAIRY_SPAWNER);
//                break;
//            case 60 * 114 - 10:
//                spawnSpawner(ecsInterface, SEX_WAVE_11_FAIRY_SPAWNER);
//                break;
//            case 60 * 119:
//                spawnSpawner(ecsInterface, SEX_WAVE_10_FAIRY_SPAWNER);
//                break;
//            case 60 * 119 + 160:
//                spawn_SEX_Wave_12_Fairy(ecsInterface, new DoublePoint(LEFT_OUT, 130), 0);
//                break;
//            case 60 * 124 + 10:
//                spawnSpawner(ecsInterface, SEX_WAVE_11_FAIRY_SPAWNER);
//                break;
//            case 60 * 124 + 10 + 160:
//                spawn_SEX_Wave_12_Fairy(ecsInterface, new DoublePoint(RIGHT_OUT, 130), 180);
//                break;
//            case 60 * 130 + 10:
//                spawnSpawner(ecsInterface, SEX_WAVE_14_FAIRY_SPAWNER);
//                break;
//            case 60 * 135 + 25:
//                spawnSpawner(ecsInterface, SEX_WAVE_15_FAIRY_SPAWNER);
//                break;
//            case 60 * 139 + 40:
//                spawn_SEX_Wave_16_Fairy(ecsInterface);
//                break;
//            case 60 * 150 + 10:
                spawn_SEX_Boss(ecsInterface);
                break;
        }
    }

    private void spawn_SEX_Wave_4_Fairy(AbstractECSInterface ecsInterface, double x) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        DoublePoint pos = new DoublePoint(x, TOP_OUT);
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(3.2, -90), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(1000)
                        .setAsFairyOrange()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_LARGE_POWER)
                        .setProgram(SEX_WAVE_4_FAIRY_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] SEX_WAVE_4_FAIRY_PROGRAM = ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
            25,
            30,
            1,
            SEX_WAVE_4_FAIRY_SPRAY,
            65,
            new PolarVector(3.2, new Angle(-90)),
            51
    ).compile();

    private void spawn_SEX_Wave_7_Wheel(AbstractECSInterface ecsInterface, double x, int health) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        DoublePoint pos = new DoublePoint(x, TOP_OUT);
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(2.5, -90), LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(health)
                        .setAsWheel()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_LARGE_POWER)
                        .setProgram(SEX_WAVE_7_WHEEL_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] SEX_WAVE_7_WHEEL_PROGRAM = ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
            35,
            80,
            1,
            SEX_WAVE_7_WHEEL_SPIRAL,
            45,
            new PolarVector(3.2, new Angle(90)),
            51
    ).compile();

    private void spawn_SEX_Midboss_Fairy(AbstractECSInterface ecsInterface) {
        DoublePoint center = new DoublePoint(WIDTH / 2d, TOP_OUT);
        AbstractVector velocity = new PolarVector(4, -90);

        ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeStraightPathCollidable(center, velocity, LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(Integer.MAX_VALUE - 1)
                        .setAsFairyOrange()
                        .setProgram(SEX_MIDBOSS_PROGRAM)
                        .setDeathSpawnWithCommandAndSpawnComponent(DROP_LIFE)
                        .packageAsMessage()
        );
    }

    private static final int MIDBOSS_LENGTH = 60 * 18;

    private static final InstructionNode<?, ?>[] SEX_MIDBOSS_PROGRAM =
            ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, 25),
                    new InstructionNode<>(SLOW_TO_HALT, 30),
                    CLEAR_FIELD,
                    new InstructionNode<>(TIMER, 30)
            ).linkAppend(
                    ProgramBuilder.circularLink(
                            new InstructionNode<>(SET_SPAWN, MBEX_PATTERN_1_1),
                            WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                            new InstructionNode<>(TIMER, 30),
                            new InstructionNode<>(SET_SPAWN, MBEX_PATTERN_1_2),
                            WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                            new InstructionNode<>(SET_VELOCITY, new PolarVector(.00001, -90)),
                            new InstructionNode<>(TIMER, 1),
                            REMOVE_VELOCITY,
                            new InstructionNode<>(TIMER, 59)
                    )
            ).noLinkInject(
                    ProgramBuilder.linearLink(
                            new InstructionNode<>(TIMER, MIDBOSS_LENGTH),
                            new InstructionNode<>(SET_HEALTH, 10000),
                            new InstructionNode<>(TIMER, 50),
                            new InstructionNode<>(SET_SPAWN, MBEX_PATTERN_1_1),
                            new InstructionNode<>(TIMER, 40),
                            new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(new PolarVector(2, 90), 90))
                    )
            ).compile();

    private void spawn_SEX_Wave_12_Fairy(AbstractECSInterface ecsInterface, DoublePoint pos, double angle) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(3.2, angle), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(20000)
                        .setAsFairyOrange()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_LARGE_POWER)
                        .setProgram(SEX_WAVE_12_FAIRY_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] SEX_WAVE_12_FAIRY_PROGRAM = ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
            25,
            30,
            1,
            SEX_WAVE_12_FAIRY_SPRAY,
            25,
            new PolarVector(3.2, new Angle(90)),
            51
    ).compile();

    private void spawn_SEX_Wave_16_Fairy(AbstractECSInterface ecsInterface) {
        DoublePoint center = new DoublePoint(WIDTH / 2d, TOP_OUT);
        AbstractVector velocity = new PolarVector(4.8, -90);

        ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeStraightPathCollidable(center, velocity, SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(25000)
                        .setAsFairyOrange()
                        .setProgram(SEX_WAVE_16_PROGRAM)
                        .setDeathSpawnWithCommandAndSpawnComponent(DROP_LIFE_AND_CLEAR)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] SEX_WAVE_16_PROGRAM =
            ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
                    0,
                    60,
                    10,
                    SEX_WAVE_16_FAIRY_SPRAY,
                    30,
                    new PolarVector(3.5, 80),
                    160
            ).compile();

    private void spawn_SEX_Boss(AbstractECSInterface ecsInterface) {
        ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeStraightPathUncollidable(
                        new DoublePoint(WIDTH / 2d, TOP_OUT),
                        new PolarVector(5, -90),
                        LARGE_ENEMY_HITBOX,
                        ENEMY_OUTBOUND
                )
                        .markAsBoss()
                        .setSpriteInstruction(new SpriteInstruction("ex_idle_1"))
                        .setAnimation(new AnimationComponent(
                                new Animation[]{
                                        new Animation(
                                                true,
                                                "ex_left_1",
                                                "ex_left_2",
                                                "ex_left_3",
                                                "ex_left_4"
                                        ),
                                        new Animation(
                                                true,
                                                "ex_idle_1",
                                                "ex_idle_2",
                                                "ex_idle_3",
                                                "ex_idle_4"
                                        ),
                                        new Animation(
                                                true,
                                                "ex_right_1",
                                                "ex_right_2",
                                                "ex_right_3",
                                                "ex_right_4"
                                        )

                                },
                                1,
                                10
                        ))
                        .setSinusoidalSpriteVerticalOffset(new SinusoidalSpriteVerticalOffsetComponent(TAU / 180, 10))
                        .setProgram(SEX_BOSS_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] SEX_BOSS_PROGRAM =
            ProgramUtil.makeBossEntryProgram(1, "ex_pre")
                    //PHASE 1: SINE SPEED SPIRAL
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(85000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(TIMER, 33),
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_1_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(120, 90, 1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 2: RAIN + LONG ANGLE STUFF
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(100000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_2_1)
                            ).linkAppend(
                                    ProgramBuilder.linearLink(
                                            new InstructionNode<>(TIMER, 92),
                                            new InstructionNode<>(ADD_SPAWN, BEX_PATTERN_2_2)
                                    ).linkAppend(
                                            ProgramUtil.makeStrictBossMoveProgramNoLoop(85, 120, 1.55)
                                    ).linkBackToFront()
                            ).noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_16),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 3: COUNTER SPIRAL SINE
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(85000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_3_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(120, 90, 1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 4 LINEAR WAVES
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(90000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_4_1_SPAWNER),
                                    WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                    new InstructionNode<>(TIMER, 42),
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_4_2)
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(55, 150, 1.55)
                            ).linkBackToFront()
                                    .noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_16),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 5: SLOW SECOND SPIRAL
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(85000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_5_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(120, 90, 1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 6 ICICLE FALL
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(100000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_6_1_SPAWNER),
                                    WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                    new InstructionNode<>(TIMER, 42),
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_6_2),
                                    WAIT_UNTIL_SPAWN_COMPONENT_EMPTY
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(55, 120, 1.55)
                            ).linkBackToFront()
                                    .noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_16),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 7: SINGLE SPIRAL
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(85000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_7_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(120, 90, 1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 8: SINE ROAD
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(110000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_8_1)
                            ).linkAppend(
                                    ProgramBuilder.linearLink(
                                            new InstructionNode<>(TIMER, 92)
                                    ).linkAppend(
                                            ProgramUtil.makeStrictBossMoveProgramNoLoop(85, 120, 1.55)
                                    ).linkBackToFront()
                            ).noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_16),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 9: SAME DIRECTION SPIRALS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(85000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_9_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(120, 90, 1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(new DoublePoint(WIDTH/2d, HEIGHT/2d), BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 10: ROTATING CONVERGING SPIRALS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(154000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_10_1)
                            ).linkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, LIFE),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0)),
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 11: STEEP WHIPS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(85000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_11_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(120, 90, 1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 12: WATER DROPS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(100000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_12_1_SPAWNER)
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(55, 120, 1.55)
                            ).linkAppend(
                                    ProgramBuilder.linearLink(
                                            WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                            new InstructionNode<>(TIMER, 70),
                                            new InstructionNode<>(SET_SPAWN, BEX_PATTERN_12_2),
                                            new InstructionNode<>(TIMER, 120)
                                    )
                            ).linkAppend(
                                    ProgramBuilder.linearLink(
                                            new InstructionNode<>(SET_SPAWN, BEX_PATTERN_12_1_SPAWNER)
                                    )
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(55, 120, 1.55)
                            ).linkAppend(
                                    ProgramBuilder.linearLink(
                                            WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                            new InstructionNode<>(TIMER, 70),
                                            new InstructionNode<>(SET_SPAWN, BEX_PATTERN_12_3),
                                            new InstructionNode<>(TIMER, 120)
                                    )
                            ).linkBackToFront()
                                    .noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_16),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 13: TRIPLE STEEP
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(85000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_13_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(120, 90, 1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 14: TOP WAVES
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(115000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_14_1),
                                    new InstructionNode<>(TIMER, 1),
                                    new InstructionNode<>(ADD_SPAWN, BEX_PATTERN_14_2)
                            ).linkAppend(
                                    ProgramBuilder.linearLink(
                                            new InstructionNode<>(TIMER, 92)
                                    ).linkAppend(
                                            ProgramUtil.makeStrictBossMoveProgramNoLoop(85, 120, 1.55)
                                    ).linkBackToFront()
                            ).noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_16),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 15: MIRROR SYMMETRY SPIRALS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(85000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_15_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(30, 60, 1.5)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 16: SNOWFLAKE
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(150000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_16_1_SPAWNER)
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(45, 270, 1.25)
                            ).linkBackToFront()
                                    .noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_16),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0)),
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(new DoublePoint(BOSS_MIDPOINT.getX(), BOSS_MIDPOINT.getY() - 70), BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(200))
                    )
                    //PHASE 17: RAINBOW
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(240000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, BEX_PATTERN_17_1)
                            ).linkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramUtil.makeBossEndProgram()
                    ).compile();
}