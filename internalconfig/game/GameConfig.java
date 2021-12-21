package internalconfig.game;

import util.math.geometry.AABB;
import util.math.geometry.AbstractVector;
import util.math.geometry.ConstCartesianVector;
import util.math.geometry.DoublePoint;

public final class GameConfig {
//FIELD OF PLAY
    public static final int WIDTH = 500;
    public static final int HEIGHT = 560;
    public static final AbstractVector OFFSET = new ConstCartesianVector(10, 20);
    public static final AbstractVector GRAPHICAL_OFFSET = new ConstCartesianVector(20, 10);

    public static final int COLLISION_OUTBOUND = 100;
    public static final AABB COLLISION_BOUNDS = new AABB(
            -COLLISION_OUTBOUND,
            WIDTH + COLLISION_OUTBOUND,
            -COLLISION_OUTBOUND,
            HEIGHT + COLLISION_OUTBOUND
    );

//SYSTEMS
    public static final int ANIMATION_TICK = 5;
    public static final double ANIMATION_VELOCITY_EPSILON = .00001;

//PLAYER
    public static final DoublePoint PLAYER_SPAWN = new DoublePoint((double)WIDTH/2, HEIGHT - 50);

    public static final AABB PLAYER_HITBOX = new AABB(2.7);
    public static final double PLAYER_SPEED = 5d;
    private static final double FOCUSED_MULTIPLIER = .4;
    public static final double FOCUSED_SPEED = PLAYER_SPEED * FOCUSED_MULTIPLIER;
    public static final double PLAYER_INBOUND = 20;

    public static final int DEATH_BOMB_PERIOD = 15;
    public static final int BOMB_INVULNERABILITY_PERIOD = 4 * 60;
    public static final int DEAD_PERIOD = 15;
    public static final int RESPAWN_PERIOD = 30;
    public static final int RESPAWN_INVULNERABILITY_PERIOD = 3 * 60;

    public static final int INIT_LIVES = 2;
    public static final int INIT_BOMBS = 2;
    public static final int INIT_CONTINUES = 3;

    public static final int MAX_LIVES = 8;
    public static final int MAX_BOMBS = MAX_LIVES;

    public static final int MAX_POWER = 128;

    public static final int CONTINUE_LIVES = 2;
    public static final int RESPAWN_BOMBS = 2;

//PLAYER SHOT
    public static final double BASIC_PLAYER_BULLET_SPEED = 16.5;
    public static final AABB BASIC_PLAYER_BULLET_HITBOX = new AABB(25);
    public static final int BASIC_PLAYER_BULLET_DAMAGE = 100;

    public static final double BASIC_PLAYER_BULLET_ANGLE_ADD = -5;

    public static final AABB SPECIAL_PLAYER_BULLET_HITBOX = new AABB(35);
    public static final double SPECIAL_PLAYER_BULLET_SPEED_NORMAL = 15;
    public static final double SPECIAL_PLAYER_BULLET_SPEED_MEDIUM = 18;
    public static final double SPECIAL_PLAYER_BULLET_SPEED_HIGH = 22;

    public static final int SPECIAL_PLAYER_BULLET_DAMAGE_NORMAL = 150;
    public static final int SPECIAL_PLAYER_BULLET_DAMAGE_MEDIUM = 165;
    public static final int SPECIAL_PLAYER_BULLET_DAMAGE_HIGH = 185;

//BULLET SLOW BARRIER
    public static final int BULLET_SLOW_TICKS_TO_MAX_SLOW = 25;

    public static final AABB BULLET_SLOW_HITBOX_SMALL = new AABB(75);
    public static final double BULLET_SLOW_MAX_SLOW_SMALL = .79;
    public static final double BULLET_SLOW_RATE_SMALL = BULLET_SLOW_MAX_SLOW_SMALL / BULLET_SLOW_TICKS_TO_MAX_SLOW;

    public static final AABB BULLET_SLOW_HITBOX_LARGE = new AABB(90);
    public static final double BULLET_SLOW_MAX_SLOW_LARGE = .77;
    public static final double BULLET_SLOW_RATE_LARGE = BULLET_SLOW_MAX_SLOW_LARGE / BULLET_SLOW_TICKS_TO_MAX_SLOW;

//PICKUPS
    public static final double PICKUP_INIT_SPEED_BASE = 3.5;
    public static final double PICKUP_INIT_SPEED_MULTI = .7;
    public static final double PICKUP_FINAL_SPEED = -4;
    public static final double PICKUP_DECELERATION = .08;
    public static final double PICKUP_INBOUND = 15;

    public static final int NUM_SMALL_PLAYER_PICKUPS = 4;
    public static final double PLAYER_PICKUP_INBOUND = 30;
    public static final double PLAYER_PICKUP_Y_HIGH = 100;
    public static final int PLAYER_PICKUP_AIR_TIME = 60;

    private static final double LARGE_PICKUP_HITBOX_RADIUS = 25;
    public static final AABB LARGE_PICKUP_HITBOX = new AABB(LARGE_PICKUP_HITBOX_RADIUS);
    public static final AABB SMALL_PICKUP_HITBOX = new AABB(LARGE_PICKUP_HITBOX_RADIUS * 4/5);

    public static final int LARGE_POWER_GAIN = 8;
    public static final int SMALL_POWER_GAIN = 1;

    public static final double PICKUP_OUTBOUND = -100;

//PROJECTILES
    public static final double NORMAL_OUTBOUND = -20;
    public static final double LARGE_OUTBOUND = -50;

//ENEMIES
    public static final double ENEMY_OUTBOUND = -50;
    public static final double ENEMY_SPAWN_INBOUND = 30;

    public static final AABB SMALL_ENEMY_HITBOX = new AABB(10);
    public static final AABB LARGE_ENEMY_HITBOX = new AABB(15);

//BOSSES
    public static final double BOSS_Y = 150;
    public static final DoublePoint BOSS_MIDPOINT = new DoublePoint(WIDTH/2d, BOSS_Y);
    public static final double BOSS_SPEED = 4d;

    public static final double BOSS_INBOUND = 60;
    public static final double BOSS_MAX_Y = HEIGHT * .28;
    public static final AABB BOSS_BOUNDS = new AABB(BOSS_INBOUND, WIDTH - BOSS_INBOUND, BOSS_INBOUND, BOSS_MAX_Y);
    public static final double BOSS_GOTO_RADIUS_MIN = 60;
    public static final double BOSS_GOTO_RADIUS_MAX = 120;
    public static final double BOSS_GOTO_SPEED = 2d;


//ENEMY PROJECTILES
    public static final int ENEMY_BULLET_COLLIDABLE_TIME = 2;

    public static final AABB SMALL_BULLET_HITBOX = new AABB(2.7);
    public static final AABB MEDIUM_BULLET_HITBOX = new AABB(6.3);
    public static final AABB LARGE_BULLET_HITBOX = new AABB(15.7);
}