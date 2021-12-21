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
import util.math.geometry.AABB;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;
import util.tuple.Tuple3;
import util.tuple.Tuple4;

import static internalconfig.game.components.spawns.PickupSpawns.BOMB;
import static internalconfig.game.components.spawns.PickupSpawns.LIFE;

import static internalconfig.game.components.spawns.PickupSpawns.POWER_10;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.TOP_OUT;
import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.*;
import static internalconfig.game.components.spawns.StageSpawns.*;
import static util.math.Constants.TAU;

class Stage2SpawnHandler extends AbstractStageSpawnHandler {

    public Stage2SpawnHandler(SpawnBuilder spawnBuilder) {
        super(spawnBuilder);
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        switch (stageTick(tick)) {
            case 0:
                spawnTrackStarter(ecsInterface, "04");
                break;
            case 73:
                spawnSpawner(ecsInterface, S2_WAVE_1_FAIRY_SPAWNER);
                break;
            case 520:
                spawnSpawner(ecsInterface, S2_WAVE_2_SMOKEBALL_SPAWNER);
                break;
            case 60 * 21 - 30:
                spawnSpawner(ecsInterface, S2_WAVE_3_ROBO_SPAWNER);
                break;
            case 60 * 26 - 30:
                spawnSpawner(ecsInterface, S2_WAVE_4_WHEEL_SMOKEBALL_SPAWNER);
                break;
            case 60 * 33:
                spawn_S2_Midboss_Spiral(ecsInterface);
                break;
            case 60 * 55:
                spawnRoboPair(ecsInterface, 80);
                break;
            case 60 * 59:
                spawnRoboPair(ecsInterface, 140);
                break;
            case 60 * 63:
                spawnRoboPair(ecsInterface, 200);
                break;
            case 60 * 66:
                spawnSpawner(ecsInterface, S2_WAVE_6_SMOKEBALL_SPAWNER);
                break;
            case 60 * 67:
                spawnRoboPair(ecsInterface, 60);
                break;
            case 60 * 71:
                spawnRoboPair(ecsInterface, 130);
                break;
            case 60 * 75:
                spawnRoboPair(ecsInterface, 200);
                break;
            case 60 * 85:
                spawn_S2_Boss(ecsInterface);
                break;
        }
    }

    private void spawn_S2_Midboss_Spiral(AbstractECSInterface ecsInterface) {
        ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeStraightPathCollidable(
                        new DoublePoint(123, TOP_OUT),
                        new PolarVector(4.5, -60),
                        new AABB(20),
                        ENEMY_OUTBOUND
                )
                        .markAsBoss()
                        .setSpriteInstruction(new SpriteInstruction("spiral_1"))
                        .setAnimation(
                                new AnimationComponent(
                                        new Animation(
                                                true,
                                                "spiral_1",
                                                "spiral_2",
                                                "spiral_3",
                                                "spiral_4",
                                                "spiral_5",
                                                "spiral_6",
                                                "spiral_7",
                                                "spiral_8"
                                        ),
                                        6
                                )
                        )
                        .setProgram(S2_MIDBOSS_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S2_MIDBOSS_PROGRAM =
            ProgramBuilder.linearLink(
                    new InstructionNode<>(TURN_TO, new Tuple2<>(new Angle(0), 80))
            )
                    .linkInject(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SLOW_TO_HALT, 130),
                                    new InstructionNode<>(SET_INBOUND, BOSS_INBOUND),
                                    new InstructionNode<>(SET_HEALTH, 52000),
                                    CLEAR_FIELD,
                                    new InstructionNode<>(TIMER, 30)
                            )
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, MB2_PATTERN_1_1)
                            )
                                    .linkAppend(
                                            ProgramUtil.makeBossMoveProgram(120, 90, 1)
                                    )
                                    .noLinkInject(
                                            ProgramUtil.makeBossDeathProgramMediumClear()
                                                    .linkAppend(ProgramBuilder.linearLink(new InstructionNode<>(SET_SPAWN, BOMB)))
                                                    .noLinkInject(ProgramUtil.makeTimerProgram(60 * 15))
                                    )
                    )
                    .linkAppendExtraNode(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_HEALTH, 4500),
                                    new InstructionNode<>(TIMER, 2)
                            )
                                    .linkAppend(
                                            ProgramBuilder.linearLink(
                                                    CLEAR_SPAWN,
                                                    new InstructionNode<>(SLOW_TO_HALT, 30),
                                                    new InstructionNode<>(REMOVE_INBOUND),
                                                    new InstructionNode<>(TIMER, 60),
                                                    new InstructionNode<>(
                                                            SPEED_UP_AND_TURN_TO_VELOCITY,
                                                            new Tuple3<>(new PolarVector(3, 30), new Angle(0), 80)
                                                    )
                                            ).noLinkInject(ProgramUtil.makeBossDeathProgramMediumClear())
                                    ),
                            1
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(TIMER, 4),
                                    new InstructionNode<>(SET_SPAWN, LIFE),
                                    new InstructionNode<>(TIMER, 5),
                                    new InstructionNode<>(SET_SPAWN, DeathSpawns.MB2_DEATH_SPIRAL_SPAWNER),
                                    new InstructionNode<>(TIMER, 2),
                                    REMOVE_ENTITY
                            )
                    )
                    .compile();

    private void spawnRoboPair(AbstractECSInterface ecsInterface, int x) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        DoublePoint spawn = new DoublePoint(x, TOP_OUT);
        SpawnUtil.mirrorFormation(spawn, WIDTH / 2d, (pos) -> sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(3.2, -90), LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(5000)
                        .setAsRobo()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER)
                        .setProgram(S2_WAVE_5_ROBO_PROGRAM)
                        .packageAsMessage()
        ));
    }

    private static final InstructionNode<?, ?>[] S2_WAVE_5_ROBO_PROGRAM = ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
            20,
            35,
            30,
            S2_WAVE_3_ROBO_RINGS,
            90,
            new PolarVector(3.2, new Angle(-90)),
            60
    ).compile();

    private void spawn_S2_Boss(AbstractECSInterface ecsInterface) {
        ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeStraightPathUncollidable(
                        new DoublePoint(WIDTH / 2d, TOP_OUT),
                        new PolarVector(5, -90),
                        LARGE_ENEMY_HITBOX,
                        ENEMY_OUTBOUND
                )
                        .markAsBoss()
                        .setSpriteInstruction(new SpriteInstruction("2_idle_1"))
                        .setAnimation(new AnimationComponent(
                                new Animation[]{
                                        new Animation(
                                                true,
                                                "2_left_1",
                                                "2_left_2",
                                                "2_left_3",
                                                "2_left_4"
                                        ),
                                        new Animation(
                                                true,
                                                "2_idle_1",
                                                "2_idle_2",
                                                "2_idle_3",
                                                "2_idle_4"
                                        ),
                                        new Animation(
                                                true,
                                                "2_right_1",
                                                "2_right_2",
                                                "2_right_3",
                                                "2_right_4"
                                                )

                                },
                                1
                        ))
                        .setSinusoidalSpriteVerticalOffset(new SinusoidalSpriteVerticalOffsetComponent(TAU/180, 10))
                        .setProgram(S2_BOSS_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S2_BOSS_PROGRAM =
            ProgramUtil.makeBossEntryProgram(30, "2_pre")
                    //PHASE 1: SPIRAL SPAWNERS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(70000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B2_PATTERN_1_1_SPAWNER),
                                    new InstructionNode<>(TIMER, 50),
                                    new InstructionNode<>(BOUND_RADIUS_GOTO_DECELERATING, new Tuple4<>(
                                            BOSS_BOUNDS,
                                            BOSS_GOTO_RADIUS_MIN,
                                            BOSS_GOTO_RADIUS_MAX,
                                            BOSS_GOTO_SPEED
                                    ))
                            ).linkInject(
                                    ProgramBuilder.linearLink(
                                            new InstructionNode<>(TIMER, 150),
                                            new InstructionNode<>(SET_SPAWN, B2_PATTERN_1_2),
                                            WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                            new InstructionNode<>(TIMER, 70)
                                    )
                            ).linkBackToFront().noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 2: ROTATING SPIRALS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(75000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B2_PATTERN_2_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(120, 90, 1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_10),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 3: AIMED SPIRALS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(62000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B2_PATTERN_3_1_SPAWNER),
                                    new InstructionNode<>(TIMER, 50),
                                    new InstructionNode<>(BOUND_RADIUS_GOTO_DECELERATING, new Tuple4<>(
                                            BOSS_BOUNDS,
                                            BOSS_GOTO_RADIUS_MIN,
                                            BOSS_GOTO_RADIUS_MAX,
                                            BOSS_GOTO_SPEED
                                    ))
                            ).linkInject(
                                    ProgramBuilder.linearLink(
                                            new InstructionNode<>(TIMER, 150),
                                            new InstructionNode<>(SET_SPAWN, B2_PATTERN_3_2),
                                            WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                            new InstructionNode<>(TIMER, 70)
                                    )
                            ).linkBackToFront().noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 4: SLOW AND SPEED SPIRAL PLUS RING
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(75000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B2_PATTERN_4_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(120, 90, 1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_10),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 5: SPIRAL OF SPIRALS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(70000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B2_PATTERN_5_1_SPAWNER)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(120, 90, 1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 6 WORMS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(95000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B2_PATTERN_6_1)
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgram(125, 125, 1.5)
                            ).noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramUtil.makeBossEndProgram()
                    )
                    .compile();

}
