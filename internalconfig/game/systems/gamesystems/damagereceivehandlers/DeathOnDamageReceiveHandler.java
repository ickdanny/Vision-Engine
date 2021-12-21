package internalconfig.game.systems.gamesystems.damagereceivehandlers;

import ecs.AbstractECSInterface;
import ecs.entity.EntityHandle;
import internalconfig.game.components.DamageReceiveCommands;
import util.messaging.Message;

import static internalconfig.game.components.DamageReceiveCommands.DEATH;
import static internalconfig.game.systems.Topics.DEATHS;

@SuppressWarnings("unused")
class DeathOnDamageReceiveHandler implements AbstractDamageReceiveHandler {
    @Override
    public DamageReceiveCommands getCommand() {
        return DEATH;
    }

    @Override
    public void handleDamageReceive(AbstractECSInterface ecsInterface, EntityHandle giver, EntityHandle receiver) {
        int messageLifetime = ecsInterface.getSliceData().getMessageLifetime();
        ecsInterface.getSliceBoard().publishMessage(new Message<>(DEATHS, receiver, messageLifetime));
    }
}