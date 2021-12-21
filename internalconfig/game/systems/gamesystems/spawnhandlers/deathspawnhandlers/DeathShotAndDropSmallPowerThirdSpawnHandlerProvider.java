package internalconfig.game.systems.gamesystems.spawnhandlers.deathspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class DeathShotAndDropSmallPowerThirdSpawnHandlerProvider
        extends AbstractDeathShotAndDropSmallPowerChanceSpawnHandlerProvider{

    public DeathShotAndDropSmallPowerThirdSpawnHandlerProvider(SpawnBuilder spawnBuilder,
                                                               AbstractComponentTypeContainer componentTypeContainer) {
        super(DeathSpawns.DEATH_SHOT_AND_DROP_SMALL_POWER_THIRD, spawnBuilder, componentTypeContainer, 1d/3);
    }
}
