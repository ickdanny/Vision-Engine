package internalconfig.game.systems.gamesystems.spawnhandlers.graphicspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.GraphicSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class MediumExplodeChartreuseSpawnHandler extends AbstractMediumExplodeSpawnHandler {
    public MediumExplodeChartreuseSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(GraphicSpawns.MEDIUM_EXPLODE_CHARTREUSE, EnemyProjectileColors.CHARTREUSE, spawnBuilder, componentTypeContainer);
    }
}