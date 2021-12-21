package internalconfig.game.systems.gamesystems.spawnhandlers;

import internalconfig.game.components.spawns.Spawns;

public abstract class AbstractSpawnHandlerTemplate implements AbstractSpawnHandler {
    protected final SpawnBuilder spawnBuilder;
    private final Spawns spawn;

    public AbstractSpawnHandlerTemplate(Spawns spawn, SpawnBuilder spawnBuilder) {
        this.spawnBuilder = spawnBuilder;
        this.spawn = spawn;
    }

    @Override
    public final Spawns getSpawn() {
        return spawn;
    }

    protected static boolean tickMod(int tick, int mod){
        return tick % mod == 0;
    }
}
