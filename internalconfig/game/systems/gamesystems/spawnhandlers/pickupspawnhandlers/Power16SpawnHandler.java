package internalconfig.game.systems.gamesystems.spawnhandlers.pickupspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

import static internalconfig.game.components.spawns.PickupSpawns.POWER_16;

public class Power16SpawnHandler extends AbstractBossBigPowerDropSpawnHandler{

    public Power16SpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(POWER_16, spawnBuilder, componentTypeContainer, 8);
    }
}