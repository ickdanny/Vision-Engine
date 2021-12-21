package internalconfig.game.systems.gamesystems.damagereceivehandlers;

import ecs.AbstractECSInterface;
import ecs.entity.EntityHandle;
import internalconfig.game.components.DamageReceiveCommands;

interface AbstractDamageReceiveHandler {
    DamageReceiveCommands getCommand();
    void handleDamageReceive(AbstractECSInterface ecsInterface, EntityHandle giver, EntityHandle receiver);
}