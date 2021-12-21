package internalconfig.game.systems.gamesystems.spawnhandlers.pickupspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

import static internalconfig.game.components.spawns.PickupSpawns.POWER_10;

public class Power10SpawnHandler extends AbstractBossBigPowerDropSpawnHandler{

    public Power10SpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(POWER_10, spawnBuilder, componentTypeContainer, 2);
    }
}