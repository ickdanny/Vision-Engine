package internalconfig.game.components;

import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.components.spawns.StageSpawns;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.CartesianVector;
import util.math.geometry.PolarVector;
import util.tuple.Tuple2;
import util.tuple.Tuple3;
import util.tuple.Tuple4;

import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.ProgramBuilder.InstructionList;

public class ProgramUtil {
    public static InstructionList makeTimerProgram(int timer) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, timer)
        );
    }

    public static InstructionList makeSetHealthProgram(int health) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(SET_HEALTH, health)
        );
    }

    public static InstructionList makeLifetimeProgram(int lifeTime) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, lifeTime),
                DIE
        );
    }

    public static InstructionList makeSetVelocityProgram(AbstractVector velocity) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(SET_VELOCITY, velocity)
        );
    }

    public static InstructionList makeEnemyAppearProgram(SpriteInstruction spriteInstruction,
                                                         AnimationComponent animationComponent) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, (2 * 7) + 1),
                SET_COLLIDABLE,
                new InstructionNode<>(SET_SPRITE_INSTRUCTION, spriteInstruction),
                new InstructionNode<>(SET_ANIMATION_INSTRUCTION, animationComponent)
        );
    }

    public static InstructionList makeBossEntryProgram(int preTimer, String dialogue) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(GOTO_DECELERATING, new Tuple2<>(BOSS_MIDPOINT, BOSS_SPEED)),
                new InstructionNode<>(SET_INBOUND, BOSS_INBOUND),
                new InstructionNode<>(TIMER, preTimer),
                new InstructionNode<>(SHOW_DIALOGUE, dialogue),
                WAIT_UNTIL_DIALOGUE_OVER,
                SET_COLLIDABLE
        );
    }

    public static InstructionList makeBossAttackAndMoveProgram(Spawns spawn, int preTimer, int postTimer) {
        return ProgramBuilder.circularLink(
                new InstructionNode<>(SET_SPAWN, spawn),
                new InstructionNode<>(TIMER, preTimer),
                new InstructionNode<>(BOUND_RADIUS_GOTO_DECELERATING, new Tuple4<>(
                        BOSS_BOUNDS,
                        BOSS_GOTO_RADIUS_MIN,
                        BOSS_GOTO_RADIUS_MAX,
                        BOSS_GOTO_SPEED
                )),
                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                new InstructionNode<>(TIMER, postTimer),
                new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
        );
    }

    public static InstructionList makeBossMoveProgram(int preTimer, int postTimer) {
        return ProgramBuilder.circularLink(
                new InstructionNode<>(TIMER, preTimer),
                new InstructionNode<>(BOUND_RADIUS_GOTO_DECELERATING, new Tuple4<>(
                        BOSS_BOUNDS,
                        BOSS_GOTO_RADIUS_MIN,
                        BOSS_GOTO_RADIUS_MAX,
                        BOSS_GOTO_SPEED
                )),
                new InstructionNode<>(TIMER, postTimer),
                new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
        );
    }

    public static InstructionList makeBossMoveProgram(int preTimer, int postTimer, double speed) {
        return ProgramBuilder.circularLink(
                new InstructionNode<>(TIMER, preTimer),
                new InstructionNode<>(BOUND_RADIUS_GOTO_DECELERATING, new Tuple4<>(
                        BOSS_BOUNDS,
                        BOSS_GOTO_RADIUS_MIN,
                        BOSS_GOTO_RADIUS_MAX,
                        speed
                )),
                new InstructionNode<>(TIMER, postTimer),
                new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
        );
    }

    //post timer must be long enough
    public static InstructionList makeStrictBossMoveProgram(int preTimer, int postTimer, double speed) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, preTimer)
        ).linkAppend(
                ProgramBuilder.linearLink(
                        new InstructionNode<>(BOUND_RADIUS_GOTO_DECELERATING, new Tuple4<>(
                                BOSS_BOUNDS,
                                BOSS_GOTO_RADIUS_MIN,
                                BOSS_GOTO_RADIUS_MAX,
                                speed
                        ))
                ).linkInject(
                        ProgramBuilder.linearLink(
                                new InstructionNode<>(TIMER, postTimer),
                                new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                        )
                )
        ).linkBackToFront();
    }

    public static InstructionList makeStrictBossMoveProgramNoLoop(int preTimer, int postTimer, double speed) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, preTimer)
        ).linkAppend(
                ProgramBuilder.linearLink(
                        new InstructionNode<>(BOUND_RADIUS_GOTO_DECELERATING, new Tuple4<>(
                                BOSS_BOUNDS,
                                BOSS_GOTO_RADIUS_MIN,
                                BOSS_GOTO_RADIUS_MAX,
                                speed
                        ))
                ).linkInject(
                        ProgramBuilder.linearLink(
                                new InstructionNode<>(TIMER, postTimer),
                                new InstructionNode<>(SET_VELOCITY, new PolarVector(0, 0))
                        )
                )
        );
    }

    public static InstructionList makeBossDeathProgram() {
        return ProgramBuilder.linearLink(
                WAIT_UNTIL_BOSS_DEATH,
                CLEAR_SPAWN,
                CLEAR_FIELD_LONG
        );
    }

    public static InstructionList makeBossDeathProgramMediumClear() {
        return ProgramBuilder.linearLink(
                WAIT_UNTIL_BOSS_DEATH,
                CLEAR_SPAWN,
                CLEAR_FIELD
        );
    }

    public static InstructionList makeBossEndProgram() {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(SET_VELOCITY, new CartesianVector(0, 0)),
                new InstructionNode<>(SET_SPAWN, DeathSpawns.BOSS_EXPLODE),
                new InstructionNode<>(ADD_SPAWN, StageSpawns.STAGE_ENDER),
                REMOVE_ENTITY
        );
    }

    public static InstructionList makeShootOnceAndLeaveEnemyProgram(int preSlowTimer,
                                                                    int slowDuration,
                                                                    int preTimer,
                                                                    Spawns spawn,
                                                                    int postTimer,
                                                                    AbstractVector finalVelocity,
                                                                    int speedDuration) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, preSlowTimer),
                new InstructionNode<>(SLOW_TO_HALT, slowDuration),
                new InstructionNode<>(TIMER, preTimer),
                new InstructionNode<>(SET_SPAWN, spawn),
                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                new InstructionNode<>(TIMER, postTimer),
                new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(finalVelocity, speedDuration))
        );
    }

    public static InstructionList makeShootOnceAndLeaveTurningEnemyProgram(int preSlowTimer,
                                                                           int slowDuration,
                                                                           int preTimer,
                                                                           Spawns spawn,
                                                                           int postTimer,
                                                                           AbstractVector finalVelocity,
                                                                           double initAngle,
                                                                           int speedDuration) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, preSlowTimer),
                new InstructionNode<>(SLOW_TO_HALT, slowDuration),
                new InstructionNode<>(TIMER, preTimer),
                new InstructionNode<>(SET_SPAWN, spawn),
                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                new InstructionNode<>(TIMER, postTimer),
                new InstructionNode<>(SPEED_UP_AND_TURN_TO_VELOCITY, new Tuple3<>(finalVelocity, new Angle(initAngle), speedDuration))
        );
    }

    public static InstructionList makeShootOnceAndLeaveTurningEnemyProgram(int preSlowTimer,
                                                                           int slowDuration,
                                                                           int preTimer,
                                                                           Spawns spawn,
                                                                           int postTimer,
                                                                           AbstractVector finalVelocity,
                                                                           Angle initAngle,
                                                                           int speedDuration) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, preSlowTimer),
                new InstructionNode<>(SLOW_TO_HALT, slowDuration),
                new InstructionNode<>(TIMER, preTimer),
                new InstructionNode<>(SET_SPAWN, spawn),
                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                new InstructionNode<>(TIMER, postTimer),
                new InstructionNode<>(SPEED_UP_AND_TURN_TO_VELOCITY, new Tuple3<>(finalVelocity, initAngle, speedDuration))
        );
    }

    public static InstructionList makeShootOnceAndLeaveLongTurningEnemyProgram(int preSlowTimer,
                                                                               int slowDuration,
                                                                               int preTimer,
                                                                               Spawns spawn,
                                                                               int postTimer,
                                                                               AbstractVector finalVelocity,
                                                                               double initAngle,
                                                                               int speedDuration) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, preSlowTimer),
                new InstructionNode<>(SLOW_TO_HALT, slowDuration),
                new InstructionNode<>(TIMER, preTimer),
                new InstructionNode<>(SET_SPAWN, spawn),
                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                new InstructionNode<>(TIMER, postTimer),
                new InstructionNode<>(SPEED_UP_AND_TURN_TO_VELOCITY_LONG_ANGLE,
                        new Tuple3<>(finalVelocity, new Angle(initAngle), speedDuration))
        );
    }

    public static InstructionList makeShootOnceAndLeaveLongTurningEnemyProgram(int preSlowTimer,
                                                                               int slowDuration,
                                                                               int preTimer,
                                                                               Spawns spawn,
                                                                               int postTimer,
                                                                               AbstractVector finalVelocity,
                                                                               Angle initAngle,
                                                                               int speedDuration) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, preSlowTimer),
                new InstructionNode<>(SLOW_TO_HALT, slowDuration),
                new InstructionNode<>(TIMER, preTimer),
                new InstructionNode<>(SET_SPAWN, spawn),
                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                new InstructionNode<>(TIMER, postTimer),
                new InstructionNode<>(SPEED_UP_AND_TURN_TO_VELOCITY_LONG_ANGLE,
                        new Tuple3<>(finalVelocity, initAngle, speedDuration))
        );
    }

    public static InstructionList makeAcceleratingBulletProgram(double speed, double acceleration) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE
        ).linkInject(
                ProgramBuilder.linearLink(
                        new InstructionNode<>(ACCELERATE_TO_SPEED, new Tuple2<>(speed, acceleration)),
                        SET_COLLIDABLE
                )
        );
    }

    public static InstructionList makeShortTurnBulletProgram(int waitTime, Angle angle, int turnTime) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE,
                new InstructionNode<>(TIMER, waitTime),
                new InstructionNode<>(TURN_TO, new Tuple2<>(angle, turnTime))
        );
    }

    public static InstructionList makeLongTurnBulletProgram(int waitTime, Angle angle, int turnTime) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE,
                new InstructionNode<>(TIMER, waitTime),
                new InstructionNode<>(TURN_TO_LONG_ANGLE, new Tuple2<>(angle, turnTime))
        );
    }

    public static InstructionList makeSlowingAndSharpTurnBulletProgram(int preSlowTime,
                                                                       int slowDuration,
                                                                       int postSlowTime,
                                                                       AbstractVector finalVelocity) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE,
                new InstructionNode<>(TIMER, preSlowTime),
                new InstructionNode<>(SLOW_TO_HALT, slowDuration),
                new InstructionNode<>(TIMER, postSlowTime),
                new InstructionNode<>(SET_VELOCITY, finalVelocity)
        );
    }

    public static InstructionList makeSlowingAndHomingBulletProgram(int preSlowTime,
                                                                    int slowDuration,
                                                                    int postSlowTime,
                                                                    double finalSpeed) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE,
                new InstructionNode<>(TIMER, preSlowTime),
                new InstructionNode<>(SLOW_TO_HALT, slowDuration),
                new InstructionNode<>(TIMER, postSlowTime),
                new InstructionNode<>(SET_VELOCITY_TO_PLAYER, finalSpeed)
        );
    }

    public static InstructionList makeSharpTurnBulletProgram(int waitTime, AbstractVector finalVelocity) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE,
                new InstructionNode<>(TIMER, waitTime),
                new InstructionNode<>(SET_VELOCITY, finalVelocity)
        );
    }
}