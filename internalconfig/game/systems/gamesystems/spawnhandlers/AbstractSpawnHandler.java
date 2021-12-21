package internalconfig.game.systems.gamesystems.spawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.spawns.Spawns;

public interface AbstractSpawnHandler {
    Spawns getSpawn();
    void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID);
}