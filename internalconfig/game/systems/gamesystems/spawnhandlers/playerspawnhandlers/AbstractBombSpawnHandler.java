package internalconfig.game.systems.gamesystems.spawnhandlers.playerspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

import static internalconfig.game.components.spawns.PlayerSpawns.BOMB;

abstract class AbstractBombSpawnHandler extends AbstractPositionSpawnHandler {
    AbstractBombSpawnHandler(SpawnBuilder spawnBuilder,
                             AbstractComponentTypeContainer componentTypeContainer) {
        super(BOMB, spawnBuilder, componentTypeContainer);
    }
}
