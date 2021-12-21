package internalconfig.game.systems.gamesystems.spawnhandlers.graphicspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

class AbstractLargeExplodeSpawnHandler extends AbstractEnemyBulletExplodeSpawnHandler {
    AbstractLargeExplodeSpawnHandler(Spawns spawn,
                                     EnemyProjectileColors color,
                                     SpawnBuilder spawnBuilder,
                                     AbstractComponentTypeContainer componentTypeContainer) {
        super(spawn, EnemyProjectileTypes.LARGE, color, 89, spawnBuilder, componentTypeContainer);
    }
}
