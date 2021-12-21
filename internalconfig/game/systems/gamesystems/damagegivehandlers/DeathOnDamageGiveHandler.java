package internalconfig.game.systems.gamesystems.damagegivehandlers;

import ecs.AbstractECSInterface;
import ecs.entity.EntityHandle;
import internalconfig.game.components.DamageGiveCommands;
import util.messaging.Message;

import static internalconfig.game.components.DamageGiveCommands.DEATH;
import static internalconfig.game.systems.Topics.*;

@SuppressWarnings("unused")
class DeathOnDamageGiveHandler implements AbstractDamageGiveHandler {

    @Override
    public DamageGiveCommands getCommand() {
        return DEATH;
    }

    @Override
    public void handleDamageGive(AbstractECSInterface ecsInterface, EntityHandle giver, EntityHandle receiver) {
        int messageLifetime = ecsInterface.getSliceData().getMessageLifetime();
        ecsInterface.getSliceBoard().publishMessage(new Message<>(DEATHS, giver, messageLifetime));
    }
}
