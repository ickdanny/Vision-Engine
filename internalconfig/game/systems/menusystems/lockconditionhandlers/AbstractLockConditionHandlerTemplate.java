package internalconfig.game.systems.menusystems.lockconditionhandlers;

import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;
import util.tuple.Tuple2;

import static internalconfig.game.systems.Topics.*;

@SuppressWarnings("SameParameterValue")
abstract class AbstractLockConditionHandlerTemplate implements AbstractLockConditionHandler {

    protected void publishLockState(AbstractPublishSubscribeBoard sliceBoard,
                                    AbstractDataStorage dataStorage,
                                    EntityHandle handle,
                                    boolean lockState){
        sliceBoard.publishMessage(
                new Message<>(NEW_LOCK_STATES, new Tuple2<>(handle, lockState), dataStorage.getMessageLifetime())
        );
    }
}
