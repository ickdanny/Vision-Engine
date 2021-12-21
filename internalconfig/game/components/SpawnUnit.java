package internalconfig.game.components;

import internalconfig.game.components.spawns.Spawns;
import util.Ticker;

public class SpawnUnit extends Ticker {
    private final Spawns spawn;

    public SpawnUnit(Spawns spawn){
        super(spawn.getDuration(), spawn.loop());
        this.spawn = spawn;
    }

    public SpawnUnit(Spawns spawn, boolean loop) {
        super(spawn.getDuration(), loop);
        this.spawn = spawn;
    }

    public Spawns getSpawn() {
        return spawn;
    }
}
