package internalconfig.game.systems.gamesystems.spawnhandlers.deathspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class DropSmallPowerFifthSpawnHandler extends AbstractDropSmallPowerChanceSpawnHandler {
    public DropSmallPowerFifthSpawnHandler(SpawnBuilder spawnBuilder,
                                           AbstractComponentTypeContainer componentTypeContainer) {
        super(DeathSpawns.DROP_SMALL_POWER_FIFTH, spawnBuilder, componentTypeContainer, 1d / 5);
    }
}
