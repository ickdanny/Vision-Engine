package internalconfig.game.systems.gamesystems.spawnhandlers;

import util.math.geometry.AABB;

import static internalconfig.game.GameConfig.*;

public enum EnemyProjectileTypes {
    SMALL("small", SMALL_BULLET_HITBOX, false),
    MEDIUM("medium", MEDIUM_BULLET_HITBOX, false),
    LARGE("large", LARGE_BULLET_HITBOX, false),
    SHARP("sharp", SMALL_BULLET_HITBOX, true),
    ;
    private final String name;
    private final AABB hitbox;
    private final boolean rotateSpriteForward;

    EnemyProjectileTypes(String name, AABB hitbox, boolean rotateSpriteForward) {
        this.name = name;
        this.hitbox = hitbox;
        this.rotateSpriteForward = rotateSpriteForward;
    }

    public String getName() {
        return name;
    }

    public AABB getHitbox() {
        return hitbox;
    }

    public boolean isRotateSpriteForward() {
        return rotateSpriteForward;
    }
}
