package internalconfig.game.systems.gamesystems.spawnhandlers.graphicspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.GraphicSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class LargeExplodeBlueSpawnHandler extends AbstractLargeExplodeSpawnHandler {
    public LargeExplodeBlueSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(GraphicSpawns.LARGE_EXPLODE_BLUE, EnemyProjectileColors.BLUE, spawnBuilder, componentTypeContainer);
    }
}