package internalconfig.game.systems.gamesystems.spawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;

class AbstractSpawnHandlerProvider {
    protected final SpawnBuilder spawnBuilder;
    protected final AbstractComponentTypeContainer componentTypeContainer;

    protected AbstractSpawnHandlerProvider(SpawnBuilder spawnBuilder,
                                           AbstractComponentTypeContainer componentTypeContainer) {
        this.spawnBuilder = spawnBuilder;
        this.componentTypeContainer = componentTypeContainer;
    }
}
