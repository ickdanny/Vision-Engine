package internalconfig.game.systems.gamesystems.spawnhandlers.graphicspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.GraphicSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class MediumExplodeCyanSpawnHandler extends AbstractMediumExplodeSpawnHandler {
    public MediumExplodeCyanSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(GraphicSpawns.MEDIUM_EXPLODE_CYAN, EnemyProjectileColors.CYAN, spawnBuilder, componentTypeContainer);
    }
}