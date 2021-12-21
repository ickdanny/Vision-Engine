package internalconfig.game.systems.gamesystems.spawnhandlers.graphicspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.GraphicSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class LargeExplodeVioletSpawnHandler extends AbstractLargeExplodeSpawnHandler {
    public LargeExplodeVioletSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(GraphicSpawns.LARGE_EXPLODE_VIOLET, EnemyProjectileColors.VIOLET, spawnBuilder, componentTypeContainer);
    }
}