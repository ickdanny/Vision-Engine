package internalconfig.game.systems.gamesystems.deathhandlers;

import ecs.AbstractECSInterface;
import ecs.entity.EntityHandle;
import internalconfig.game.components.DeathCommands;

interface AbstractDeathHandler {
    DeathCommands getCommand();
    void handleDeath(AbstractECSInterface ecsInterface, EntityHandle deadEntity);
}
