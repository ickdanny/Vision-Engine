package internalconfig.game.systems.gamesystems.spawnhandlers.deathspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class DropSmallPowerHalfSpawnHandler extends AbstractDropSmallPowerChanceSpawnHandler {
    public DropSmallPowerHalfSpawnHandler(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(DeathSpawns.DROP_SMALL_POWER_HALF, spawnBuilder, componentTypeContainer, 1d/2);
    }
}
