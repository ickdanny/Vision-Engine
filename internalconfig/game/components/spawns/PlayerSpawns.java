package internalconfig.game.components.spawns;

public enum PlayerSpawns implements Spawns_PP {
    SHOT(PlayerSpawns.SHOT_DURATION),
    BOMB(60 * 2),
    BOMB_A_EXPLOSION(1),
    PLAYER_BULLET_BLOCKER(1),
    PLAYER_DEATH_PICKUPS(1),
    ;
    public static final int SHOT_DURATION = 30;

    private final int duration;
    private int index;

    PlayerSpawns(int duration){
        this.duration = duration;
        index = INVALID_INDEX;
    }

    @Override
    public int getDuration() {
        return duration;
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