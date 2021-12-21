package internalconfig.game.systems.gamesystems.spawnhandlers.graphicspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.GraphicSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class SmallExplodeYellowSpawnHandler extends AbstractSmallExplodeSpawnHandler {
    public SmallExplodeYellowSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(GraphicSpawns.SMALL_EXPLODE_YELLOW, EnemyProjectileColors.YELLOW, spawnBuilder, componentTypeContainer);
    }
}
