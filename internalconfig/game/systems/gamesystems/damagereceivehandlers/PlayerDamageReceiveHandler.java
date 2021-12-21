package internalconfig.game.systems.gamesystems.damagereceivehandlers;

import ecs.AbstractECSInterface;
import ecs.entity.EntityHandle;
import internalconfig.game.components.DamageReceiveCommands;
import util.messaging.Message;

import static internalconfig.game.systems.Topics.PLAYER_HITS;

import static internalconfig.game.components.DamageReceiveCommands.PLAYER_DAMAGE;

class PlayerDamageReceiveHandler implements AbstractDamageReceiveHandler {
    @Override
    public DamageReceiveCommands getCommand() {
        return PLAYER_DAMAGE;
    }

    @Override
    public void handleDamageReceive(AbstractECSInterface ecsInterface, EntityHandle giver, EntityHandle receiver) {
        int messageLifetime = ecsInterface.getSliceData().getMessageLifetime();
        ecsInterface.getSliceBoard().publishMessage(new Message<>(PLAYER_HITS, null, messageLifetime));
    }
}