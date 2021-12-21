package internalconfig.game.systems.gamesystems.spawnhandlers.graphicspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.GraphicSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class LargeExplodeOrangeSpawnHandler extends AbstractLargeExplodeSpawnHandler {
    public LargeExplodeOrangeSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(GraphicSpawns.LARGE_EXPLODE_ORANGE, EnemyProjectileColors.ORANGE, spawnBuilder, componentTypeContainer);
    }
}