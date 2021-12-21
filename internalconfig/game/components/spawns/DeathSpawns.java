package internalconfig.game.components.spawns;

public enum DeathSpawns implements Spawns_PP{
    BOSS_EXPLODE,

    DROP_SMALL_POWER,
    DROP_SMALL_POWER_HALF,
    DROP_SMALL_POWER_THIRD,
    DROP_SMALL_POWER_FOURTH,
    DROP_SMALL_POWER_FIFTH,
    DROP_SMALL_POWER_SIXTH,
    DEATH_SHOT_AND_DROP_SMALL_POWER,
    DEATH_SHOT_AND_DROP_SMALL_POWER_THIRD,
    DEATH_SHOT_AND_DROP_SMALL_POWER_FOURTH,

    DROP_LARGE_POWER,

    DROP_BOMB,
    DROP_LIFE,
    DROP_LIFE_AND_CLEAR,

    MB2_DEATH_SPIRAL_SPAWNER,
    ;

    private int index;

    DeathSpawns(){
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
}
