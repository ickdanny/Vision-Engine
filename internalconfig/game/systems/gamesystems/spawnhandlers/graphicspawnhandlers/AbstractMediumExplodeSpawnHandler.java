package internalconfig.game.systems.gamesystems.spawnhandlers.graphicspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

class AbstractMediumExplodeSpawnHandler extends AbstractEnemyBulletExplodeSpawnHandler {
    AbstractMediumExplodeSpawnHandler(Spawns spawn,
                                      EnemyProjectileColors color,
                                      SpawnBuilder spawnBuilder,
                                      AbstractComponentTypeContainer componentTypeContainer) {
        super(spawn, EnemyProjectileTypes.MEDIUM, color, 88, spawnBuilder, componentTypeContainer);
    }
}
