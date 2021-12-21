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
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.PickupSpawns.POWER_10;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.*;
import static internalconfig.game.components.spawns.DeathSpawns.*;
import static internalconfig.game.components.spawns.StageSpawns.*;
import static util.math.Constants.TAU;

class Stage1SpawnHandler extends AbstractStageSpawnHandler {

    public Stage1SpawnHandler(SpawnBuilder spawnBuilder) {
        super(spawnBuilder);
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        switch (stageTick(tick)) {
            case 40:
                spawnTrackStarter(ecsInterface, "02");
                break;
            case 60 * 11 + 30:
                spawn_S1_Wave_1_Fairy_1(ecsInterface);
                break;
            case 60 * 13 - 15:
                spawn_S1_Wave_1_Fairy_2(ecsInterface);
                break;
            case 60 * 14:
                spawn_S1_Wave_1_Fairy_3(ecsInterface);
                break;
            case 60 * 18:
                spawnSpawner(ecsInterface, S1_WAVE_2_SMOKEBALL_SPAWNER);
                break;
            case 60 * 27:
                spawnSpawner(ecsInterface, S1_WAVE_3_BAT_WHEEL_SPAWNER);
                break;
            case 60 * 38 + 10:
                spawnSpawner(ecsInterface, S1_WAVE_4_FAIRY_WING_SPAWNER);
                break;
            case 60 * 44:
                spawn_S1_Wave_5_Fairy(ecsInterface);
                break;
            case 60 * 51:
                spawnSpawner(ecsInterface, S1_WAVE_6_BAT_SPAWNER);
                break;
            case 60 * 56 - 20:
                spawn_S1_Wave_7_Fairy(ecsInterface);
                break;
            case 60 * 63 - 20:
                spawnSpawner(ecsInterface, S1_WAVE_6_BAT_SPAWNER);
                break;
            case 60 * 69:
                spawnSpawner(ecsInterface, S1_WAVE_9_WHEEL_SPAWNER);
                break;
            case 60 * 108:
                spawn_S1_Boss(ecsInterface);
                break;
        }
    }


    private void spawn_S1_Wave_1_Fairy_1(AbstractECSInterface ecsInterface) {
        DoublePoint center = new DoublePoint(WIDTH / 2d, TOP_OUT);
        SpawnUtil.blockFormation(center, WIDTH, 8, (p) -> {
            AbstractVector velocity = new PolarVector(4, -90);

            ecsInterface.getSliceBoard().publishMessage(
                    spawnBuilder.makeStraightPathCollidable(p, velocity, SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                            .markAsMob(300)
                            .setAsFairyRed()
                            .setProgram(S1_WAVE_1_1_PROGRAM)
                            .setDeathSpawnWithCommandAndSpawnComponent(DROP_SMALL_POWER)
                            .packageAsMessage()
            );
        });
    }

    private static final InstructionNode<?, ?>[] S1_WAVE_1_1_PROGRAM =
            ProgramUtil.makeShootOnceAndLeaveTurningEnemyProgram(
                    0,
                    90,
                    0,
                    S1_WAVE_1_FAIRY_OPENING_1,
                    40,
                    new PolarVector(4, -170),
                    -90,
                    80
            ).compile();

    private void spawn_S1_Wave_1_Fairy_2(AbstractECSInterface ecsInterface) {
        DoublePoint center = new DoublePoint(WIDTH / 2d, TOP_OUT);
        SpawnUtil.blockFormation(center, WIDTH - (WIDTH / 9d), 7, (p) -> {
            AbstractVector velocity = new PolarVector(4, -90);

            ecsInterface.getSliceBoard().publishMessage(
                    spawnBuilder.makeStraightPathCollidable(p, velocity, SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                            .markAsMob(300)
                            .setAsFairyOrange()
                            .setProgram(S1_WAVE_1_2_PROGRAM)
                            .setDeathSpawnWithCommandAndSpawnComponent(DROP_SMALL_POWER)
                            .packageAsMessage()
            );
        });
    }

    private static final InstructionNode<?, ?>[] S1_WAVE_1_2_PROGRAM =
            ProgramUtil.makeShootOnceAndLeaveTurningEnemyProgram(
                    0,
                    90,
                    0,
                    S1_WAVE_1_FAIRY_OPENING_2,
                    40,
                    new PolarVector(4, -10),
                    -90,
                    80
            ).compile();

    private void spawn_S1_Wave_1_Fairy_3(AbstractECSInterface ecsInterface) {
        DoublePoint center = new DoublePoint(WIDTH / 2d, TOP_OUT);
        SpawnUtil.blockFormation(center, WIDTH, 8, (p) -> {
            AbstractVector velocity = new PolarVector(4, -90);

            ecsInterface.getSliceBoard().publishMessage(
                    spawnBuilder.makeStraightPathCollidable(p, velocity, SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                            .markAsMob(300)
                            .setAsFairyYellow()
                            .setProgram(S1_WAVE_1_3_PROGRAM)
                            .setDeathSpawnWithCommandAndSpawnComponent(DROP_SMALL_POWER)
                            .packageAsMessage()
            );
        });
    }

    private static final InstructionNode<?, ?>[] S1_WAVE_1_3_PROGRAM =
            ProgramUtil.makeShootOnceAndLeaveTurningEnemyProgram(
                    0,
                    90,
                    0,
                    S1_WAVE_1_FAIRY_OPENING_3,
                    40,
                    new PolarVector(4, -170),
                    -90,
                    80
            ).compile();

    private void spawn_S1_Wave_5_Fairy(AbstractECSInterface ecsInterface) {
        DoublePoint center = new DoublePoint(WIDTH / 2d, TOP_OUT);
        SpawnUtil.blockFormation(center, WIDTH, 6, (p) -> {
            AbstractVector velocity = new PolarVector(4, -90);

            ecsInterface.getSliceBoard().publishMessage(
                    spawnBuilder.makeStraightPathCollidable(p, velocity, SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                            .markAsMob(2000)
                            .setAsFairyRed()
                            .setProgram(S1_WAVE_5_PROGRAM)
                            .setDeathSpawnWithCommandAndSpawnComponent(DROP_SMALL_POWER_HALF)
                            .packageAsMessage()
            );
        });
    }

    private static final InstructionNode<?, ?>[] S1_WAVE_5_PROGRAM =
            ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
                    0,
                    90,
                    0,
                    S1_WAVE_5_FAIRY_ACCELERATING_RINGS,
                    60,
                    new PolarVector(3, 90),
                    160
            ).compile();

    private void spawn_S1_Wave_7_Fairy(AbstractECSInterface ecsInterface) {
        DoublePoint center = new DoublePoint(WIDTH / 2d, TOP_OUT);
        SpawnUtil.blockFormation(center, WIDTH, 6, (p) -> {
            AbstractVector velocity = new PolarVector(4, -90);

            ecsInterface.getSliceBoard().publishMessage(
                    spawnBuilder.makeStraightPathCollidable(p, velocity, SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                            .markAsMob(2000)
                            .setAsFairyRed()
                            .setProgram(S1_WAVE_7_PROGRAM)
                            .setDeathSpawnWithCommandAndSpawnComponent(DROP_SMALL_POWER_HALF)
                            .packageAsMessage()
            );
        });
    }

    private static final InstructionNode<?, ?>[] S1_WAVE_7_PROGRAM =
            ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
                    0,
                    90,
                    0,
                    S1_WAVE_7_FAIRY_ACCELERATING_RINGS,
                    60,
                    new PolarVector(3, 90),
                    160
            ).compile();

    private void spawn_S1_Boss(AbstractECSInterface ecsInterface) {
        ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeStraightPathUncollidable(
                        new DoublePoint(WIDTH / 2d, TOP_OUT),
                        new PolarVector(5, -90),
                        LARGE_ENEMY_HITBOX,
                        ENEMY_OUTBOUND
                )
                        .markAsBoss()
                        .setSpriteInstruction(new SpriteInstruction("1_idle"))
                        .setAnimation(new AnimationComponent(
                                new Animation[]{
                                        new Animation(true, "1_left"),
                                        new Animation(true, "1_idle"),
                                        new Animation(true, "1_right")
                                },
                                1
                        ))
                        .setSinusoidalSpriteVerticalOffset(new SinusoidalSpriteVerticalOffsetComponent(TAU/180, 10))
                        .setProgram(S1_BOSS_PROGRAM)
                        .packageAsMessage()
        );
    }

    private static final InstructionNode<?, ?>[] S1_BOSS_PROGRAM =
            ProgramUtil.makeBossEntryProgram(30, "1_pre")
                    //PHASE 1: WHIP/SPIRAL
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(40000)
                    )
                    .linkAppend(
                            ProgramUtil.makeBossAttackAndMoveProgram(
                                    B1_PATTERN_1_1,
                                    B1_PATTERN_1_1.getDuration() - 30,
                                    60
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 2: RINGS AND COLUMNS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(50000)
                    )
                    .linkAppend(
                            ProgramUtil.makeBossAttackAndMoveProgram(
                                    B1_PATTERN_2_1,
                                    B1_PATTERN_2_1.getDuration() + 60,
                                    90
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, POWER_10),
                                    new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                            ).linkInject(ProgramUtil.makeTimerProgram(120))
                    )
                    //PHASE 3: WHIP/SPIRAL
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(50000)
                    )
                    .linkAppend(
                            ProgramUtil.makeBossAttackAndMoveProgram(
                                    B1_PATTERN_3_1,
                                    B1_PATTERN_3_1.getDuration() - 120,
                                    90
                            ).noLinkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED * .66))
                            ).linkInject(ProgramUtil.makeTimerProgram(180))
                    )
                    //PHASE 4: COLUMN FIELD WITH AIMED ARCS
                    .linkAppend(
                            ProgramUtil.makeSetHealthProgram(66250)
                    )
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(SET_SPAWN, B1_PATTERN_4_1)
                            ).linkInject(ProgramUtil.makeBossDeathProgram())
                    )
                    .linkAppend(
                            ProgramUtil.makeBossEndProgram()
                    )
                    .compile();
}