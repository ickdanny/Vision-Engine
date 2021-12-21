package internalconfig.game.systems.gamesystems.deathhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.DeathCommands;
import internalconfig.game.systems.Topics;
import util.messaging.Message;

import static internalconfig.game.components.DeathCommands.BOSS_DEATH;

class BossDeathHandler implements AbstractDeathHandler {

    @Override
    public DeathCommands getCommand() {
        return BOSS_DEATH;
    }

    @Override
    public void handleDeath(AbstractECSInterface ecsInterface, EntityHandle deadEntity) {
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        ecsInterface.getSliceBoard().publishMessage(
                new Message<>(Topics.BOSS_DEATH, null, dataStorage.getMessageLifetime())
        );
    }
}
