package internalconfig.game.systems.gamesystems.damagegivehandlers;

import ecs.AbstractECSInterface;
import ecs.entity.EntityHandle;
import internalconfig.game.components.DamageGiveCommands;

interface AbstractDamageGiveHandler {
    DamageGiveCommands getCommand();
    void handleDamageGive(AbstractECSInterface ecsInterface, EntityHandle giver, EntityHandle receiver);
}