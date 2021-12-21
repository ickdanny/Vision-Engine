package internalconfig.game.systems.gamesystems.spawnhandlers.pickupspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

import static internalconfig.game.components.spawns.PickupSpawns.POWER_12;

public class Power12SpawnHandler extends AbstractBossBigPowerDropSpawnHandler{

    public Power12SpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(POWER_12, spawnBuilder, componentTypeContainer, 4);
    }
}