package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.Animation;
import internalconfig.game.components.AnimationComponent;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.components.SinusoidalSpriteVerticalOffsetComponent;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.tuple.Tuple2;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.StageSpawns.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.*;
import static internalconfig.game.components.spawns.DeathSpawns.*;
import static internalconfig.game.components.spawns.PickupSpawns.POWER_12;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;
import static internalconfig.game.components.Instructions.*;
import static util.math.Constants.TAU;

class Stage3SpawnHandler extends AbstractStageSpawnHandler {

    private static final double WING_SPEED = 2.2;

    public Stage3SpawnHandler(SpawnBuilder spawnBuilder) {
        super(spawnBuilder);
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        switch (stageTick(tick)) {
            case 0:
                spawnTrackStarter(ecsInterface, "06");
                break;
            case 120:
                spawnSpawner(ecsInterface, S3_WAVE_1_SMOKEBALL_SPAWNER);
                break;
            case 60 * 10:
                spawnSpawner(ecsInterface, S3_WAVE_2_FAIRY_SPAWNER);
                break;
            case 60 * 14:
                spawnSpawner(ecsInterface, S3_WAVE_3_FAIRY_SPAWNER);
                break;
            case 60 * 19 - 30:
                spawn_S3_Wave_4_Wing_1(ecsInterface);
                break;
            case 60 * 20:
                spawn_S3_Wave_4_Wing_2(ecsInterface);
                break;
            case 60 * 21 + 30:
                spawn_S3_Wave_4_Wing_3(ecsInterface);
                break;
            case 60 * 25:
                spawnSpawner(ecsInterface, S3_WAVE_5_VIRION_SPAWNER);
                break;
            case 60 * 32:
            case 60 * 37:
                spawn_S3_Wave_6_Wing(ecsInterface);
                break;
            case 60 * 42:
                spawnSpawner(ecsInterface, S3_WAVE_7_FAIRY_SPAWNER);
                break;
            case 60 * 44:
                spawn_S3_Wave_8_Fairy(ecsInterface);
                break;
            case 60 * 46:
                spawnSpawner(ecsInterface, S3_WAVE_9_FAIRY_SPAWNER);
                break;
            case 60 * 48:
                spawn_S3_Wave_8_Fairy(ecsInterface);
                break;
            case 60 * 50:
                spawn_S3_Midboss_Wing(ecsInterface);
                break;
            case 60 * 60:
                spawnSpawner(ecsInterface, S3_WAVE_11_SMOKEBALL_VIRION_SPAWNER);
                break;
            case 60 * 78:
                spawn_S3_Wave_6_Wing(ecsInterface);
                break;
            case 60 * 80:
                spawnSpawner(ecsInterface, S3_WAVE_2_FAIRY_SPAWNER);
                break;
            case 60 * 82:
                spawn_S3_Wave_6_Wing(ecsInterface);
                break;
            case 60 * 84:
                spawnSpawner(ecsInterface, S3_WAVE_3_FAIRY_SPAWNER);
                break;
            case 60 * 88:
                spawnSpawner(ecsInterface, S3_WAVE_16_FAIRY_SPAWNER);
                break;
            case 60 * 92:
                spawnSpawner(ecsInterface, S3_WAVE_17_FAIRY_SPAWNER);
                break;
            case 60 * 98:
                spawn_S3_Boss(ecsInterface);
                break;
        }
    }

    private void spawn_S3_Wave_4_Wing_1(AbstractECSInterface ecsInterface) {
        DoublePoint basePos = new DoublePoint(78, TOP_OUT);
        SpawnUtil.mirrorFormation(basePos, WIDTH / 2d, (pos) -> {
            AbstractVector velocity = new PolarVector(2.5, -90);

            ecsInterface.getSliceBoard().publishMessage(
                    spawnBuilder.makeStraightPathCollidable(pos, velocity, LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                            .markAsMob(4000)
                            .setAsWing()
                            .setProgram(S3_WAVE_4_PROGRAM_1)
                            .setDeathSpawnWithCommandAndSpawnComponent(DROP_SMALL_POWER)
                            .packageAsMessage()
            );
        });
    }

    private static final InstructionNode<?, ?>[] S3_WAVE_4_PROGRAM_1 =
            ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
                    0,
                    60,
                    0,
                    S3_WAVE_4_WING_DOWN_WHIP_ARC,
                    60,
                    new PolarVector(WING_SPEED, -90),
                    160
            ).compile();

    private void spawn_S3_Wave_4_Wing_2(AbstractECSInterface ecsInterface) {
        DoublePoint basePos = new DoublePoint(144, TOP_OUT);
        SpawnUtil.mirrorFormation(basePos, WIDTH / 2d, (pos) -> {
            AbstractVector velocity = new PolarVector(2.5, -90);

            ecsInterface.getSliceBoard().publishMessage(
                    spawnBuilder.makeStraightPathCollidable(pos, velocity, LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                            .markAsMob(4000)
                            .setAsWing()
                            .setProgram(S3_WAVE_4_PROGRAM_2)
                            .setDeathSpawnWithCommandAndSpawnComponent(DROP_SMALL_POWER)
                            .packageAsMessage()
            );
        });
    }

    private static final InstructionNode<?, ?>[] S3_WAVE_4_PROGRAM_2 =
            ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
                    0,
                    60,
                    0,
                    S3_WAVE_4_WING_SHOTGUN,
                    60,
                    new PolarVector(WING_SPEED, -90),
                    160
            ).compile();

    private void spawn_S3_Wave_4_Wing_3(AbstractECSInterface ecsInterface) {
        DoublePoint pos = new DoublePoint(WIDTH / 2d, TOP_OUT);
        AbstractVector velocity = new PolarVector(2.5, -90);

        ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, velocity, LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(8000)
                        .setAsWing()
                        .setProgram(S3_WAVE_4_PROGRAM_3)
                        .setDeathSpawnWithCommandAndSpawnComponent(DROP_LARGE_POWER)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S3_WAVE_4_PROGRAM_3 =
            ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
                    0,
                    60,
                    0,
                    S3_WAVE_4_WING_MIRROR_ARCS,
                    90,
                    new PolarVector(WING_SPEED, -90),
                    160
            ).compile();

    private void spawn_S3_Wave_6_Wing(AbstractECSInterface ecsInterface) {
        DoublePoint pos = new DoublePoint(LEFT_OUT, 30);
        AbstractVector velocity = new PolarVector(2, -10);

        SpawnUtil.mirrorFormation(pos, velocity, WIDTH / 2d, (p, v) -> ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeStraightPathCollidable(p, v, LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(4000)
                        .setAsWing()
                        .setProgram(S3_WAVE_5_PROGRAM)
                        .setDeathSpawnWithCommandAndSpawnComponent(DROP_SMALL_POWER)
                        .packageAsMessage()
        ));
    }

    private static final InstructionNode<?, ?>[] S3_WAVE_5_PROGRAM =
            ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, 70),
                    new InstructionNode<>(SET_SPAWN, S3_WAVE_6_WING_ARCS)
            ).compile();

    private void spawn_S3_Wave_8_Fairy(AbstractECSInterface ecsInterface) {
        AbstractVector velocity = new PolarVector(4, -90);

        SpawnUtil.mirrorFormation(new DoublePoint(80, TOP_OUT), WIDTH / 2d, (p) -> ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeStraightPathCollidable(p, velocity, SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(1000)
                        .setAsFairyYellow()
                        .setProgram(S3_WAVE_8_PROGRAM)
                        .setDeathSpawnWithCommandAndSpawnComponent(DROP_SMALL_POWER_HALF)
                        .packageAsMessage()
        ));

        SpawnUtil.mirrorFormation(new DoublePoint(170, TOP_OUT), WIDTH / 2d, (p) -> ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeStraightPathCollidable(p, velocity, SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(1000)
                        .setAsFairyYellow()
                        .setProgram(S3_WAVE_8_PROGRAM)
                        .setDeathSpawnWithCommandAndSpawnComponent(DROP_SMALL_POWER_HALF)
                        .packageAsMessage()
        ));
    }

    private static final InstructionNode<?, ?>[] S3_WAVE_8_PROGRAM =
            ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
                    0,
                    40,
                    0,
                    S3_WAVE_8_FAIRY_SPRAY,
                    90,
                    new PolarVector(2.5, -90),
                    160
            ).compile();

    private void spawn_S3_Midboss_Wing(AbstractECSInterface ecsInterface) {
        DoublePoint center = new DoublePoint(WIDTH / 2d, TOP_OUT);
        AbstractVector velocity = new PolarVector(5, -90);

        ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeStraightPathCollidable(center, velocity, LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(30000)
                        .setAsWing()
                        .setProgram(S3_MIDBOSS_PROGRAM)
                        .setDeathSpawnWithCommandAndSpawnComponent(DROP_LIFE)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S3_MIDBOSS_PROGRAM =
            ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
                    0,
                    80,
                    0,
                    MB3_PATTERN_1_1,
                    30,
                    new PolarVector(2.5, 90),
                    160
            ).compile();

    private void spawn_S3_Boss(AbstractECSInterface ecsInterface) {
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
                                                "3_left_1",
                                                "3_left_2",
                                                "3_left_3",
                                                "3_left_4"
                                        ),
                                        new Animation(
                                                true,
                                                "3_idle_1",
                                                "3_idle_2",
                                                "3_idle_3",
                                                "3_idle_4"
                                        ),
                                        new Animation(
                                                true,
                                                "3_right_1",
                                                "3_right_2",
                                                "3_right_3",
                                                "3_right_4"
                                        )

                                },
                                1
                        ))
                        .setSinusoidalSpriteVerticalOffset(new SinusoidalSpriteVerticalOffsetComponent(TAU / 180, 10))
                        .setProgram(S3_BOSS_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S3_BOSS_PROGRAM =
            ProgramUtil.makeBossEntryProgram(120, "3_pre")
                    //PHASE 1: HOMING SEMI-ARCS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(85000)
                    )
                    .linkAppend(
                            ProgramUtil.makeBossAttackAndMoveProgram(
                                    B3_PATTERN_1_1,
                                    B3_PATTERN_1_1.getDuration() - 150,
                                    80
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 2: SEA
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(90000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B3_PATTERN_2_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(60, 90, 1.1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_12),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 3: SQUARES
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(85000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B3_PATTERN_3_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(60, 90, 1.1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 4: TRIPLE ARCS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(90000)
                    )
                    .linkAppend(
                            ProgramUtil.makeBossAttackAndMoveProgram(
                                    B3_PATTERN_4_1,
                                    B3_PATTERN_4_1.getDuration() - 150,
                                    40
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_12),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 5: CURVING SEMI ARCS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(80000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B3_PATTERN_5_1)
                            ).linkAppend(
                                    ProgramUtil.makeBossMoveProgram(120, 90, 1)
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 6 TURNING RING + WHIP RING
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(95000)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B3_PATTERN_6_1)
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(135, 90, 1.75)

                            ).linkAppend(
                                    ProgramBuilder.linearLink(
                                            new InstructionNode<>(SET_SPAWN, B3_PATTERN_6_2)
                                    )
                            ).linkAppend(
                                    ProgramUtil.makeStrictBossMoveProgramNoLoop(65, 90, 1.75)
                            ).linkBackToFront().noLinkInjectIfPossible(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramUtil.makeBossEndProgram()
                    )
                    .compile();
}
