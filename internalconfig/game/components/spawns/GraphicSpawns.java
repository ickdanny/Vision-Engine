package internalconfig.game.components.spawns;

import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes;

public enum GraphicSpawns implements Spawns_PP{
    BASIC_EXPLODE,
    SPECIAL_NORMAL_EXPLODE,
    SPECIAL_MEDIUM_EXPLODE,
    SPECIAL_HIGH_EXPLODE,

    SMALL_EXPLODE_RED,
    SMALL_EXPLODE_ORANGE,
    SMALL_EXPLODE_YELLOW,
    SMALL_EXPLODE_CHARTREUSE,
    SMALL_EXPLODE_GREEN,
    SMALL_EXPLODE_SPRING,
    SMALL_EXPLODE_CYAN,
    SMALL_EXPLODE_AZURE,
    SMALL_EXPLODE_BLUE,
    SMALL_EXPLODE_VIOLET,
    SMALL_EXPLODE_MAGENTA,
    SMALL_EXPLODE_ROSE,

    MEDIUM_EXPLODE_RED,
    MEDIUM_EXPLODE_ORANGE,
    MEDIUM_EXPLODE_YELLOW,
    MEDIUM_EXPLODE_CHARTREUSE,
    MEDIUM_EXPLODE_GREEN,
    MEDIUM_EXPLODE_SPRING,
    MEDIUM_EXPLODE_CYAN,
    MEDIUM_EXPLODE_AZURE,
    MEDIUM_EXPLODE_BLUE,
    MEDIUM_EXPLODE_VIOLET,
    MEDIUM_EXPLODE_MAGENTA,
    MEDIUM_EXPLODE_ROSE,

    LARGE_EXPLODE_RED,
    LARGE_EXPLODE_ORANGE,
    LARGE_EXPLODE_YELLOW,
    LARGE_EXPLODE_CHARTREUSE,
    LARGE_EXPLODE_GREEN,
    LARGE_EXPLODE_SPRING,
    LARGE_EXPLODE_CYAN,
    LARGE_EXPLODE_AZURE,
    LARGE_EXPLODE_BLUE,
    LARGE_EXPLODE_VIOLET,
    LARGE_EXPLODE_MAGENTA,
    LARGE_EXPLODE_ROSE,
    ;

    private int index;

    GraphicSpawns(){
        index = INVALID_INDEX;
    }

    @Override
    public int getDuration() {
        return 1;
    }

    @Override
    public boolean loop() {
        return false;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        if(this.index != INVALID_INDEX){
            throw new RuntimeException("cannot set index twice!");
        }
        this.index = index;
    }

    public static GraphicSpawns getEnemyBulletExplodeSpawn(EnemyProjectileTypes type, EnemyProjectileColors color){
        String typeName;
        if(type != EnemyProjectileTypes.SHARP){
            typeName = type.getName().toUpperCase();
        }
        else{
            typeName = EnemyProjectileTypes.SMALL.getName().toUpperCase();
        }
        String colorName = color.getSuffix().toUpperCase();
        return valueOf(typeName + "_EXPLODE" + colorName);
    }

}