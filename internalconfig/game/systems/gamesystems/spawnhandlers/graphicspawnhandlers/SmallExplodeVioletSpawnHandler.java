package internalconfig.game.systems.gamesystems.spawnhandlers.graphicspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.GraphicSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class SmallExplodeVioletSpawnHandler extends AbstractSmallExplodeSpawnHandler {
    public SmallExplodeVioletSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(GraphicSpawns.SMALL_EXPLODE_VIOLET, EnemyProjectileColors.VIOLET, spawnBuilder, componentTypeContainer);
    }
}
