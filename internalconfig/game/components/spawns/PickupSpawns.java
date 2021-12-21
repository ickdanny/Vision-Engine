package internalconfig.game.components.spawns;

public enum PickupSpawns implements Spawns_PP{
    POWER_10,
    POWER_12,
    POWER_14,
    POWER_16,
    LIFE,
    BOMB
    ;

    private int index;

    PickupSpawns(){
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
