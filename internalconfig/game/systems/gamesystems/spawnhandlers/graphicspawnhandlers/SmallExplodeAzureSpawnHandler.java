package internalconfig.game.systems.gamesystems.spawnhandlers.graphicspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.GraphicSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class SmallExplodeAzureSpawnHandler extends AbstractSmallExplodeSpawnHandler {
    public SmallExplodeAzureSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(GraphicSpawns.SMALL_EXPLODE_AZURE, EnemyProjectileColors.AZURE, spawnBuilder, componentTypeContainer);
    }
}
