package internalconfig.game.systems.gamesystems.spawnhandlers.deathspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class DropSmallPowerThirdSpawnHandler extends AbstractDropSmallPowerChanceSpawnHandler {
    public DropSmallPowerThirdSpawnHandler(SpawnBuilder spawnBuilder,
                                           AbstractComponentTypeContainer componentTypeContainer) {
        super(DeathSpawns.DROP_SMALL_POWER_THIRD, spawnBuilder, componentTypeContainer, 1d/3);
    }
}
