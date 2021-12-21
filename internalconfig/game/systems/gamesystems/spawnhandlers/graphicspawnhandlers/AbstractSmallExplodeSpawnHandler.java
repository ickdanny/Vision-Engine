package internalconfig.game.systems.gamesystems.spawnhandlers.graphicspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

class AbstractSmallExplodeSpawnHandler extends AbstractEnemyBulletExplodeSpawnHandler {
    AbstractSmallExplodeSpawnHandler(Spawns spawn,
                                     EnemyProjectileColors color,
                                     SpawnBuilder spawnBuilder,
                                     AbstractComponentTypeContainer componentTypeContainer) {
        super(spawn, EnemyProjectileTypes.SMALL, color, 87, spawnBuilder, componentTypeContainer);
    }
}
