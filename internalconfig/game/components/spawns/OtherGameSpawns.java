package internalconfig.game.components.spawns;

@SuppressWarnings("SameParameterValue")
public enum OtherGameSpawns implements Spawns_PP{
    BULLET_CLEAR(1, false),
    MEDIUM_BULLET_CLEAR(1, false),
    LONG_BULLET_CLEAR(1, false),
    ;
    private final int duration;
    private final boolean loop;
    private int index;

    OtherGameSpawns(int duration, boolean loop){
        this.duration = duration;
        this.loop = loop;
        index = INVALID_INDEX;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public boolean loop() {
        return loop;
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
