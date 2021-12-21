package internalconfig.game.systems.gamesystems.spawnhandlers.pickupspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

import static internalconfig.game.components.spawns.PickupSpawns.POWER_14;

public class Power14SpawnHandler extends AbstractBossBigPowerDropSpawnHandler{

    public Power14SpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(POWER_14, spawnBuilder, componentTypeContainer, 6);
    }
}