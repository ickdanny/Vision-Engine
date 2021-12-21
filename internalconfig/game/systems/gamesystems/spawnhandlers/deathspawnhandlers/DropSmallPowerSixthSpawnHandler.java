package internalconfig.game.systems.gamesystems.spawnhandlers.deathspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class DropSmallPowerSixthSpawnHandler extends AbstractDropSmallPowerChanceSpawnHandler {
    public DropSmallPowerSixthSpawnHandler(SpawnBuilder spawnBuilder,
                                           AbstractComponentTypeContainer componentTypeContainer) {
        super(DeathSpawns.DROP_SMALL_POWER_SIXTH, spawnBuilder, componentTypeContainer, 1d/6);
    }
}
