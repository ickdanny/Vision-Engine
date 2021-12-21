package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.Animation;
import internalconfig.game.components.AnimationComponent;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.components.SinusoidalSpriteVerticalOffsetComponent;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.*;
import static internalconfig.game.components.spawns.DeathSpawns.*;
import static internalconfig.game.components.spawns.PickupSpawns.*;
import static internalconfig.game.components.spawns.StageSpawns.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;
import static util.math.Constants.*;

class Stage6SpawnHandler extends AbstractStageSpawnHandler {

    public Stage6SpawnHandler(SpawnBuilder spawnBuilder) {
        super(spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        switch (stageTick(tick)) {
            case 0:
                spawnTrackStarter(ecsInterface, "12");
                break;
            case 60 * 12 + 20:
                spawnSpawner(ecsInterface, S6_WAVE_1_FLAME_SPAWNER);
                break;
            case 60 * 24:
                spawn_S6_Wave_2_Apparition(ecsInterface, 70);
                break;
            case 60 * 34:
                spawn_S6_Wave_2_Apparition(ecsInterface, 110);
                break;
            case 60 * 44 - 20:
                spawn_S6_Midboss_Apparition(ecsInterface);
                break;
            case 60 * 68:
                spawn_S6_Boss(ecsInterface);
                break;
        }
    }

    private static final double S6_WAVE_2_TOTAL_WIDTH = 350;
    private static final int S6_WAVE_2_SPAWNS = 5;
    private static final double S6_WAVE_2_X_LOW = 185;
    private static final double S6_WAVE_2_X_HIGH = WIDTH - S6_WAVE_2_X_LOW;

    private static final double S6_WAVE_2_Y_RANGE = 15;

    private static final int S6_WAVE_2_HEALTH = 20000;

    private static final int S6_WAVE_2_APPARITION_PRE_ATTACK_TIME = 10;
    private static final int S6_WAVE_2_APPARITION_POST_TIMER = 1;
    private static final int S6_WAVE_2_APPARITION_TRANSPARENT_DURATION = 50;

    private void spawn_S6_Wave_2_Apparition(AbstractECSInterface ecsInterface, double baseY) {
        AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        Random random = GameUtil.getRandom(globalBoard);

        double x = RandomUtil.randDoubleInclusive(S6_WAVE_2_X_LOW, S6_WAVE_2_X_HIGH, random);
        double y = baseY + RandomUtil.randDoubleInclusive(-S6_WAVE_2_Y_RANGE, S6_WAVE_2_Y_RANGE, random);
        DoublePoint basePos = new DoublePoint(x, y);

        SpawnUtil.blockFormation(basePos, S6_WAVE_2_TOTAL_WIDTH, S6_WAVE_2_SPAWNS, (p) -> spawnApparition(sliceBoard, p, make_S6_Wave_2_Apparition_Program()));
    }

    private void spawnApparition(AbstractPublishSubscribeBoard sliceBoard,
                                 DoublePoint pos,
                                 InstructionNode<?, ?>[] program) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStationaryUncollidable(pos, SMALL_ENEMY_HITBOX)
                        .markAsMob(S6_WAVE_2_HEALTH)
                        .setAsAppearAnimation()
                        .setDeathSpawnWithCommandAndSpawnComponent(DROP_LARGE_POWER)
                        .setProgram(program)
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] make_S6_Wave_2_Apparition_Program() {
        return ProgramUtil.makeEnemyAppearProgram(SpawnUtil.makeApparitionSpriteInstruction(), SpawnUtil.makeApparitionAnimationComponent())
                .linkAppend(
                        ProgramBuilder.linearLink(
                                new InstructionNode<>(TIMER, S6_WAVE_2_APPARITION_PRE_ATTACK_TIME),
                                new InstructionNode<>(SET_SPAWN, S6_WAVE_2_APPARITION_COLUMNS),
                                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                new InstructionNode<>(TIMER, S6_WAVE_2_APPARITION_POST_TIMER),
                                new InstructionNode<>(SHIFT_TRANSPARENCY_OVER_PERIOD, new Tuple2<>(.5, S6_WAVE_2_APPARITION_TRANSPARENT_DURATION / 2)),
                                REMOVE_COLLIDABLE,
                                new InstructionNode<>(SHIFT_TRANSPARENCY_OVER_PERIOD, new Tuple2<>(0d, S6_WAVE_2_APPARITION_TRANSPARENT_DURATION / 2)),
                                REMOVE_ENTITY
                        )
                ).compile();
    }

    private void spawn_S6_Midboss_Apparition(AbstractECSInterface ecsInterface) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        DoublePoint pos = new DoublePoint(WIDTH / 2d, 100.5);
        sliceBoard.publishMessage(
                spawnBuilder.makeStationaryUncollidable(pos, SMALL_ENEMY_HITBOX)
                        .markAsMob(95000)
                        .setAsAppearAnimation()
                        .setDeathSpawnWithCommandAndSpawnComponent(DROP_BOMB)
                        .setProgram(make_S6_Midboss_Apparition_Program())
                        .packageAsMessage()
        );
    }

    private static final int S6_MIDBOSS_APPARITION_PRE_ATTACK_TIME = 10;
    private static final int S6_MIDBOSS_APPARITION_POST_TIMER = 1;
    private static final int S6_MIDBOSS_APPARITION_TRANSPARENT_DURATION = 110;

    private InstructionNode<?, ?>[] make_S6_Midboss_Apparition_Program() {
        return ProgramUtil.makeEnemyAppearProgram(SpawnUtil.makeApparitionSpriteInstruction(), SpawnUtil.makeApparitionAnimationComponent())
                .linkAppend(
                        ProgramBuilder.linearLink(
                                new InstructionNode<>(TIMER, S6_MIDBOSS_APPARITION_PRE_ATTACK_TIME),
                                new InstructionNode<>(SET_SPAWN, MB6_PATTERN_1_1),
                                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                new InstructionNode<>(TIMER, S6_MIDBOSS_APPARITION_POST_TIMER),
                                new InstructionNode<>(SHIFT_TRANSPARENCY_OVER_PERIOD, new Tuple2<>(.5, S6_MIDBOSS_APPARITION_TRANSPARENT_DURATION / 2)),
                                REMOVE_COLLIDABLE,
                                new InstructionNode<>(SHIFT_TRANSPARENCY_OVER_PERIOD, new Tuple2<>(0d, S6_MIDBOSS_APPARITION_TRANSPARENT_DURATION / 2)),
                                REMOVE_ENTITY
                        )
                ).compile();
    }

    private void spawn_S6_Boss(AbstractECSInterface ecsInterface) {
        ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeStraightPathUncollidable(
                        new DoublePoint(WIDTH / 2d, TOP_OUT),
                        new PolarVector(5, -90),
                        LARGE_ENEMY_HITBOX,
                        ENEMY_OUTBOUND
                )
                        .markAsBoss()
                        .setSpriteInstruction(new SpriteInstruction("6_idle_1"))
                        .setAnimation(new AnimationComponent(
                                new Animation[]{
                                        new Animation(
                                                true,
                                                "6_left_1",
                                                "6_left_2",
                                                "6_left_3",
                                                "6_left_4"
                                        ),
                                        new Animation(
                                                true,
                                                "6_idle_1",
                                                "6_idle_2"
                                        ),
                                        new Animation(
                                                true,
                                                "6_right_1",
                                                "6_right_2",
                                                "6_right_3",
                                                "6_right_4"
                                        )

                                },
                                1,
                                10
                        ))
                        .setSinusoidalSpriteVerticalOffset(new SinusoidalSpriteVerticalOffsetComponent(TAU / 180, 10))
                        .setProgram(S6_BOSS_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S6_BOSS_PROGRAM =
            ProgramUtil.makeBossEntryProgram(30, "6_pre")
                    //PHASE 1: FIELD AND STRAIGHT LARGE
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(100000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B6_PATTERN_1_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(120, 90, 1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 2: SIDE DIAGONAL LARGE AND TRIPLE SPIRAL
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(100000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B6_PATTERN_2_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(120, 90, 1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_16),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0)),
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 3: RINGS AND LARGE RANDOM SPAWN
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(100000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B6_PATTERN_3_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossDeathProgram()
                            )
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 4: STARS ABOVE
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(100000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B6_PATTERN_4_1_SPAWNER)
                            ).linkAppend(
                                    ProgramBuilder.linearLink(
                                            new InstructionNode<>(TIMER, 92),
                                            new InstructionNode<>(ADD_SPAWN, B6_PATTERN_4_2),
                                            new InstructionNode<>(TIMER, 32)
                                    ).linkAppend(
                                            ProgramUtil.makeStrictBossMoveProgramNoLoop(55, 120, 1.55)
                                    ).linkBackToFront()
                            ).noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_16),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0)),
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 5: WALLS FIELD AND CURVING BIG
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(100000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B6_PATTERN_5_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(120, 90, 1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 6 "LASERS" AND BIG AIM
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(90000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B6_PATTERN_6_1),
                                    WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                    new InstructionNode<>(SET_SPAWN, B6_PATTERN_6_2),
                                    new InstructionNode<>(TIMER, 24)
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(55, 142, 1.55)
                            ).linkBackToFront()
                                    .noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_16),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    .linkAppend(ProgramUtil.makeTimerProgram(120))
                    //PHASE 7 STARS RING
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(100000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B6_PATTERN_7_1_SPAWNER)
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(55, 120, 1.55)
                            ).linkBackToFront()
                                    .noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 8: CIRCLES
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(120000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B6_PATTERN_8_1),
                                    new InstructionNode<>(TIMER, 60),
                                    new InstructionNode<>(ADD_SPAWN, B6_PATTERN_8_2),
                                    new InstructionNode<>(TIMER, 140),
                                    new InstructionNode<>(ADD_SPAWN, B6_PATTERN_8_2),
                                    new InstructionNode<>(TIMER, 140),
                                    new InstructionNode<>(ADD_SPAWN, B6_PATTERN_8_2),
                                    new InstructionNode<>(TIMER, 140),
                                    new InstructionNode<>(ADD_SPAWN, B6_PATTERN_8_2),
                                    new InstructionNode<>(TIMER, 140),
                                    new InstructionNode<>(ADD_SPAWN, B6_PATTERN_8_3),
                                    WAIT_UNTIL_SPAWN_COMPONENT_EMPTY
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(55, 120, 1.55)
                            ).linkBackToFront()
                                    .noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_16),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0)),
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 9 SOLAR FALL
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(200000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B6_PATTERN_9_1_SPAWNER)
                            ).linkAppend(
                                    ProgramBuilder.linearLink(
                                            new InstructionNode<>(TIMER, 92),
                                            new InstructionNode<>(ADD_SPAWN, B6_PATTERN_9_2),
                                            new InstructionNode<>(TIMER, 32)
                                    ).linkAppend(
                                            ProgramUtil.makeStrictBossMoveProgramNoLoop(55, 120, 1.55)
                                    ).linkBackToFront()
                            ).noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramUtil.makeBossEndProgram()
                    )
                    .compile();

}
