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
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.*;
import static internalconfig.game.components.spawns.DeathSpawns.*;
import static internalconfig.game.components.spawns.PickupSpawns.POWER_14;
import static internalconfig.game.components.spawns.StageSpawns.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;
import static util.math.Constants.TAU;

class Stage4SpawnHandler extends AbstractStageSpawnHandler {

    public Stage4SpawnHandler(SpawnBuilder spawnBuilder) {
        super(spawnBuilder);
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        switch (stageTick(tick)) {
            case 30:
                spawnTrackStarter(ecsInterface, "08");
                break;
            case 90:
                spawnSpawner(ecsInterface, S4_WAVE_1_FLAME_SPAWNER);
                break;
            case 60 * 9 + 30:
                spawnSpawner(ecsInterface, S4_WAVE_2_FLAME_SPAWNER);
                break;
            case 60 * 17 + 30:
                spawnSpawner(ecsInterface, S4_WAVE_3_FLAME_SPAWNER);
                break;
            case 60 * 19 + 30:
                spawnSpawner(ecsInterface, S4_WAVE_4_FLAME_SPAWNER);
                break;
            case 60 * 21 - 20:
                spawn_S4_Wave_5_Spike(ecsInterface, 90);
                break;
            case 60 * 22 - 20:
                spawn_S4_Wave_5_Spike(ecsInterface, WIDTH - 90);
                break;
            case 60 * 23 - 20:
                spawn_S4_Wave_5_Spike(ecsInterface, WIDTH / 2d);
                break;
            case 60 * 25 - 25:
                spawnSpawner(ecsInterface, S4_WAVE_6_SPIKE_SPAWNER);
                break;
            case 60 * 42:
                spawnSpawner(ecsInterface, S4_WAVE_7_APPARITION_SPAWNER);
                break;
            case 60 * 46:
                spawnSpawner(ecsInterface, S4_WAVE_8_APPARITION_SPAWNER);
                break;
            case 60 * 50:
                spawnSpawner(ecsInterface, S4_WAVE_9_APPARITION_SPAWNER);
                break;
            case 60 * 62 - 10:
                spawn_S4_Wave_10_Apparition_1(ecsInterface);
                break;
            case 60 * 70 - 10:
                spawn_S4_Wave_10_Apparition_2(ecsInterface);
                break;
            case 60 * 78:
                spawn_S4_Wave_10_Apparition_3(ecsInterface);
                break;
            case 60 * 91 - 20:
                spawnSpawner(ecsInterface, S4_WAVE_2_FLAME_SPAWNER);
                break;
            case 60 * 99 - 20:
                spawnSpawner(ecsInterface, S4_WAVE_1_FLAME_SPAWNER);
                break;
            case 60 * 107 - 20:
                spawnSpawner(ecsInterface, S4_WAVE_4_FLAME_SPAWNER);
                break;
            case 60 * 109 - 20:
                spawnSpawner(ecsInterface, S4_WAVE_3_FLAME_SPAWNER);
                break;
            case 60 * 111 - 70:
                spawn_S4_Wave_15_Spike(ecsInterface, WIDTH - 90);
                break;
            case 60 * 112 - 70:
                spawn_S4_Wave_15_Spike(ecsInterface, 90);
                break;
            case 60 * 113 - 70:
                spawn_S4_Wave_15_Spike(ecsInterface, WIDTH/2d);
                break;
            case 60 * 117 - 75:
                spawn_S4_Boss(ecsInterface);
                break;
        }
    }

    private void spawn_S4_Wave_5_Spike(AbstractECSInterface ecsInterface, double x) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        DoublePoint pos = new DoublePoint(x, TOP_OUT);
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(3.2, -90), LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(7000)
                        .setAsSpike()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER)
                        .setProgram(S4_WAVE_5_SPIKE_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S4_WAVE_5_SPIKE_PROGRAM = ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
            30,
            32,
            1,
            S4_WAVE_5_SPIKE_RING,
            65,
            new PolarVector(3.2, new Angle(-90)),
            51
    ).compile();

    private static final int S4_WAVE_10_APPARITION_PRE_ATTACK_TIME = 10;
    private static final int S4_WAVE_10_APPARITION_POST_TIMER = 1;
    private static final int S4_WAVE_10_APPARITION_TRANSPARENT_DURATION = 110;
    private static final int S4_WAVE_10_APPARITION_HEALTH = 25000;

    private void spawn_S4_Wave_10_Apparition_1(AbstractECSInterface ecsInterface) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        DoublePoint pos = new DoublePoint(114.52, 123.5);
        sliceBoard.publishMessage(
                spawnBuilder.makeStationaryUncollidable(pos, SMALL_ENEMY_HITBOX)
                        .markAsMob(S4_WAVE_10_APPARITION_HEALTH)
                        .setAsAppearAnimation()
                        .setDeathSpawnWithCommandAndSpawnComponent(DROP_LARGE_POWER)
                        .setProgram(make_S4_Wave_10_Apparition_1_Program())
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] make_S4_Wave_10_Apparition_1_Program() {
        return ProgramUtil.makeEnemyAppearProgram(SpawnUtil.makeApparitionSpriteInstruction(), SpawnUtil.makeApparitionAnimationComponent())
                .linkAppend(
                        ProgramBuilder.linearLink(
                                new InstructionNode<>(TIMER, S4_WAVE_10_APPARITION_PRE_ATTACK_TIME),
                                new InstructionNode<>(SET_SPAWN, S4_WAVE_10_APPARITION_PATTERN_1),
                                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                new InstructionNode<>(TIMER, S4_WAVE_10_APPARITION_POST_TIMER),
                                new InstructionNode<>(SHIFT_TRANSPARENCY_OVER_PERIOD, new Tuple2<>(.5, S4_WAVE_10_APPARITION_TRANSPARENT_DURATION / 2)),
                                REMOVE_COLLIDABLE,
                                new InstructionNode<>(SHIFT_TRANSPARENCY_OVER_PERIOD, new Tuple2<>(0d, S4_WAVE_10_APPARITION_TRANSPARENT_DURATION / 2)),
                                REMOVE_ENTITY
                        )
                ).compile();
    }

    private void spawn_S4_Wave_10_Apparition_2(AbstractECSInterface ecsInterface) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        DoublePoint pos = new DoublePoint(WIDTH - 114.52, 123.5);
        sliceBoard.publishMessage(
                spawnBuilder.makeStationaryUncollidable(pos, SMALL_ENEMY_HITBOX)
                        .markAsMob(S4_WAVE_10_APPARITION_HEALTH)
                        .setAsAppearAnimation()
                        .setDeathSpawnWithCommandAndSpawnComponent(DROP_BOMB)
                        .setProgram(make_S4_Wave_10_Apparition_2_Program())
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] make_S4_Wave_10_Apparition_2_Program() {
        return ProgramUtil.makeEnemyAppearProgram(SpawnUtil.makeApparitionSpriteInstruction(), SpawnUtil.makeApparitionAnimationComponent())
                .linkAppend(
                        ProgramBuilder.linearLink(
                                new InstructionNode<>(TIMER, S4_WAVE_10_APPARITION_PRE_ATTACK_TIME),
                                new InstructionNode<>(SET_SPAWN, S4_WAVE_10_APPARITION_PATTERN_2),
                                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                new InstructionNode<>(TIMER, S4_WAVE_10_APPARITION_POST_TIMER),
                                new InstructionNode<>(SHIFT_TRANSPARENCY_OVER_PERIOD, new Tuple2<>(.5, S4_WAVE_10_APPARITION_TRANSPARENT_DURATION / 2)),
                                REMOVE_COLLIDABLE,
                                new InstructionNode<>(SHIFT_TRANSPARENCY_OVER_PERIOD, new Tuple2<>(0d, S4_WAVE_10_APPARITION_TRANSPARENT_DURATION / 2)),
                                REMOVE_ENTITY
                        )
                ).compile();
    }

    private void spawn_S4_Wave_10_Apparition_3(AbstractECSInterface ecsInterface) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        DoublePoint pos = new DoublePoint(WIDTH / 2d, 123.5);
        sliceBoard.publishMessage(
                spawnBuilder.makeStationaryUncollidable(pos, SMALL_ENEMY_HITBOX)
                        .markAsMob(45000)
                        .setAsAppearAnimation()
                        .setDeathSpawnWithCommandAndSpawnComponent(DROP_LIFE)
                        .setProgram(make_S4_Wave_10_Apparition_3_Program())
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] make_S4_Wave_10_Apparition_3_Program() {
        return ProgramUtil.makeEnemyAppearProgram(SpawnUtil.makeApparitionSpriteInstruction(), SpawnUtil.makeApparitionAnimationComponent())
                .linkAppend(
                        ProgramBuilder.linearLink(
                                new InstructionNode<>(TIMER, S4_WAVE_10_APPARITION_PRE_ATTACK_TIME),
                                new InstructionNode<>(SET_SPAWN, S4_WAVE_10_APPARITION_PATTERN_3),
                                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                new InstructionNode<>(TIMER, S4_WAVE_10_APPARITION_POST_TIMER),
                                new InstructionNode<>(SHIFT_TRANSPARENCY_OVER_PERIOD, new Tuple2<>(.5, S4_WAVE_10_APPARITION_TRANSPARENT_DURATION / 2)),
                                REMOVE_COLLIDABLE,
                                new InstructionNode<>(SHIFT_TRANSPARENCY_OVER_PERIOD, new Tuple2<>(0d, S4_WAVE_10_APPARITION_TRANSPARENT_DURATION / 2)),
                                REMOVE_ENTITY
                        )
                ).compile();
    }

    private void spawn_S4_Wave_15_Spike(AbstractECSInterface ecsInterface, double x) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        DoublePoint pos = new DoublePoint(x, TOP_OUT);
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(3.2, -90), LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(7000)
                        .setAsSpike()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER)
                        .setProgram(S4_WAVE_15_SPIKE_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S4_WAVE_15_SPIKE_PROGRAM = ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
            30,
            32,
            1,
            S4_WAVE_15_SPIKE_RING,
            65,
            new PolarVector(3.2, new Angle(90)),
            51
    ).compile();

    private void spawn_S4_Boss(AbstractECSInterface ecsInterface) {
        ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeStraightPathUncollidable(
                        new DoublePoint(WIDTH / 2d, TOP_OUT),
                        new PolarVector(5, -90),
                        LARGE_ENEMY_HITBOX,
                        ENEMY_OUTBOUND
                )
                        .markAsBoss()
                        .setSpriteInstruction(new SpriteInstruction("4_idle"))
                        .setAnimation(new AnimationComponent(
                                new Animation[]{
                                        new Animation(true, "4_left"),
                                        new Animation(true, "4_idle"),
                                        new Animation(true, "4_right")
                                },
                                1
                        ))
                        .setSinusoidalSpriteVerticalOffset(new SinusoidalSpriteVerticalOffsetComponent(TAU / 180, 10))
                        .setProgram(S4_BOSS_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S4_BOSS_PROGRAM =
            ProgramUtil.makeBossEntryProgram(30, "4_pre")
                    //PHASE 1: OPPOSITE SPIRALS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(100000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B4_PATTERN_1_1),
                                    WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                    new InstructionNode<>(SET_SPAWN, B4_PATTERN_1_2)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(160, 190, 1.1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 2: FRONTAL SLASH
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(100000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B4_PATTERN_2_1),
                                    WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                    new InstructionNode<>(TIMER, 5),
                                    new InstructionNode<>(SET_SPAWN, B4_PATTERN_2_2)
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(55, 120, 1.55)
                            ).linkBackToFront().noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_14),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 3: SAME DIRECTION SPIRALS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(100000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B4_PATTERN_3_1),
                                    WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                    new InstructionNode<>(SET_SPAWN, B4_PATTERN_3_2)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(160, 190, 1.1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 4: CELLS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(130000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B4_PATTERN_4_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(160, 190, 1.1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_14),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 5: SINE SPIRAL
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(100000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B4_PATTERN_5_1),
                                    WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                    new InstructionNode<>(SET_SPAWN, B4_PATTERN_5_2)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(120, 90, 1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 6 WAVY AND BIG
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(100000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B4_PATTERN_6_1_SPAWNER),
                                    WAIT_UNTIL_SPAWN_COMPONENT_EMPTY
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(15, 70, 1.85)
                            ).linkAppend(
                                    ProgramBuilder.linearLink(
                                            new InstructionNode<>(SET_SPAWN, B4_PATTERN_6_2),
                                            WAIT_UNTIL_SPAWN_COMPONENT_EMPTY
                                    )
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(30, 60, 1.75)
                            ).linkBackToFront().noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                    )
                    .linkAppend(ProgramUtil.makeTimerProgram(120))
                    //PHASE 7 CURVING CIRCLES AND RAIN
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(110000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B4_PATTERN_7_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(160, 190, 1.1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramUtil.makeBossEndProgram()
                    )
                    .compile();
}

