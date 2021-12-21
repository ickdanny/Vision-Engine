package internalconfig.game.components.spawns;

public enum MiscSpawns implements Spawns_PP{
    ;

    private final int duration;
    private int index;

    MiscSpawns(int duration){
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
