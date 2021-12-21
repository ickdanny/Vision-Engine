package internalconfig.game.components;

import internalconfig.game.components.spawns.Spawns;
import util.Ticker;
import util.math.geometry.AABB;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.tuple.Tuple2;
import util.tuple.Tuple3;
import util.tuple.Tuple4;

import java.util.ArrayList;
import java.util.List;

public final class Instructions<T, V> implements ProgramBuilder.ProgramBuilderPassable<T, V> {

    private static int nextIndex = 0;
    private static final List<Instructions<?, ?>> VALUES = new ArrayList<>();

    public static final Instructions<Integer, Ticker> TIMER = new Instructions<>();

    public static final Instructions<Void, Void> REMOVE_VISIBLE = new Instructions<>();

    //target transparency, ticks
    public static final Instructions<Tuple2<Double, Integer>, Double> SHIFT_TRANSPARENCY_OVER_PERIOD = new Instructions<>();
    //target size, ticks
    public static final Instructions<Tuple2<Double, Integer>, Double> SHIFT_SCALE_OVER_PERIOD = new Instructions<>();

    public static final Instructions<SpriteInstruction, Void> SET_SPRITE_INSTRUCTION = new Instructions<>();
    public static final Instructions<AnimationComponent, Void> SET_ANIMATION_INSTRUCTION = new Instructions<>();
    public static final Instructions<Integer, Void> SET_DRAW_ORDER = new Instructions<>();

    public static final Instructions<Void, Object> WAIT_UNTIL_SPAWN_COMPONENT_EMPTY = new Instructions<>();
    public static final Instructions<Void, Void> WAIT_UNTIL_PLAYER_FOCUSED = new Instructions<>();
    public static final Instructions<Void, Void> WAIT_UNTIL_PLAYER_UNFOCUSED = new Instructions<>();
    public static final Instructions<Void, Void> WAIT_UNTIL_PLAYER_NORMAL = new Instructions<>();
    public static final Instructions<Void, Void> WAIT_UNTIL_PLAYER_BOMBING = new Instructions<>();
    public static final Instructions<Void, Void> WAIT_UNTIL_PLAYER_DEAD = new Instructions<>();
    public static final Instructions<Void, Void> WAIT_UNTIL_PLAYER_RESPAWNING = new Instructions<>();
    public static final Instructions<Void, Void> WAIT_UNTIL_PLAYER_RESPAWN_INVULNERABLE = new Instructions<>();
    public static final Instructions<Void, Void> WAIT_UNTIL_BOSS_DEATH = new Instructions<>();
    public static final Instructions<Void, Void> WAIT_UNTIL_DIALOGUE_OVER = new Instructions<>();

    public static final Instructions<Double, Void> BOUNDARY_Y_LOW = new Instructions<>();
    public static final Instructions<Double, Void> BOUNDARY_Y_HIGH = new Instructions<>();
    public static final Instructions<Double, Void> BOUNDARY_X_LOW = new Instructions<>();
    public static final Instructions<Double, Void> BOUNDARY_X_HIGH = new Instructions<>();
    public static final Instructions<Double, Void> BOUNDARY_Y = new Instructions<>();
    public static final Instructions<Double, Void> BOUNDARY_X = new Instructions<>();
    public static final Instructions<Double, Void> BOUNDARY = new Instructions<>();

    public static final Instructions<Void, Void> SET_COLLIDABLE = new Instructions<>();
    public static final Instructions<Void, Void> REMOVE_COLLIDABLE = new Instructions<>();

    public static final Instructions<Integer, Void> SET_HEALTH = new Instructions<>();
    public static final Instructions<Void, Void> REMOVE_HEALTH = new Instructions<>();

    public static final Instructions<Integer, Void> SET_DAMAGE = new Instructions<>();
    public static final Instructions<Void, Void> REMOVE_DAMAGE = new Instructions<>();

    public static final Instructions<Spawns, Void> SET_SPAWN = new Instructions<>();
    public static final Instructions<Spawns, Void> ADD_SPAWN = new Instructions<>();
    public static final Instructions<Void, Void> CLEAR_SPAWN = new Instructions<>(); //removing spawn results in error with deathSpawns

    public static final Instructions<AbstractVector, Void> SET_VELOCITY = new Instructions<>();
    public static final Instructions<Double, Void> SET_VELOCITY_TO_PLAYER = new Instructions<>();
    //speedLow, speedHigh, angleLow, angleHigh
    public static final Instructions<Tuple4<Double, Double, Double, Double>, Void> SET_RANDOM_VELOCITY = new Instructions<>();
    public static final Instructions<Void, Void> REMOVE_VELOCITY = new Instructions<>();

    public static final Instructions<Double, Void> SET_INBOUND = new Instructions<>();
    public static final Instructions<Void, Void> REMOVE_INBOUND = new Instructions<>();

    public static final Instructions<Double, Void> SET_OUTBOUND = new Instructions<>();

    public static final Instructions<Integer, Double> SLOW_TO_HALT = new Instructions<>();
    //velocity, ticks
    public static final Instructions<Tuple2<AbstractVector, Integer>, Double> SPEED_UP_TO_VELOCITY = new Instructions<>();
    //velocity, initAngle, ticks
    public static final Instructions<Tuple3<AbstractVector, Angle, Integer>, Tuple2<Double, Double>>
            SPEED_UP_AND_TURN_TO_VELOCITY = new Instructions<>();
    public static final Instructions<Tuple3<AbstractVector, Angle, Integer>, Tuple2<Double, Double>>
            SPEED_UP_AND_TURN_TO_VELOCITY_LONG_ANGLE = new Instructions<>();
    //velocity, ticks
    public static final Instructions<Tuple2<AbstractVector, Integer>, Double> SLOW_DOWN_TO_VELOCITY = new Instructions<>();
    public static final Instructions<Tuple2<AbstractVector, Integer>, Tuple2<Double, Double>>
            SLOW_DOWN_AND_TURN_TO_VELOCITY = new Instructions<>();
    public static final Instructions<Tuple2<AbstractVector, Integer>, Tuple2<Double, Double>>
            SLOW_DOWN_AND_TURN_TO_VELOCITY_LONG_ANGLE = new Instructions<>();

    //angle, ticks
    public static final Instructions<Tuple2<Angle, Integer>, Double> TURN_TO = new Instructions<>();
    public static final Instructions<Tuple2<Angle, Integer>, Double> TURN_TO_LONG_ANGLE = new Instructions<>();

    //target speed, acceleration
    public static final Instructions<Tuple2<Double, Double>, Void> ACCELERATE_TO_SPEED = new Instructions<>();
    //target speed, deceleration
    public static final Instructions<Tuple2<Double, Double>, Void> DECELERATE_TO_SPEED = new Instructions<>();

    //target position, maximum speed, storing init distance
    public static final Instructions<Tuple2<DoublePoint, Double>, Double> GOTO_DECELERATING = new Instructions<>();
    //bounds, minimum radius, maximum radius, maximum speed, storing target and init distance
    public static final Instructions<Tuple4<AABB, Double, Double, Double>, Tuple2<DoublePoint, Double>>
            BOUND_RADIUS_GOTO_DECELERATING = new Instructions<>();

    public static final Instructions<Void, Void> FOLLOW_PLAYER = new Instructions<>();

    public static final Instructions<Void, Void> CLEAR_FIELD = new Instructions<>();
    public static final Instructions<Void, Void> CLEAR_FIELD_LONG = new Instructions<>();

    public static final Instructions<Void, Void> DIE = new Instructions<>();
    public static final Instructions<Void, Void> REMOVE_ENTITY = new Instructions<>();

    public static final Instructions<String, Void> START_TRACK = new Instructions<>();

    public static final Instructions<String, Void> SHOW_DIALOGUE = new Instructions<>();

    public static final Instructions<Void, Void> NEXT_STAGE = new Instructions<>();

    public static final Instructions<Void, Void> GAME_OVER = new Instructions<>();
    public static final Instructions<Void, Void> GAME_WIN = new Instructions<>();

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final int index;

    private Instructions(){
        index = nextIndex++;
        VALUES.add(this);
    }

    public int getIndex() {
        return index;
    }

    public static Instructions<?, ?>[] values(){
        return VALUES.toArray(new Instructions<?, ?>[0]);
    }
}