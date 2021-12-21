package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.game.components.Instructions.DIE;
import static internalconfig.game.systems.Topics.DEATHS;

@SuppressWarnings("unused")
class DieInstructionHandler implements AbstractInstructionHandler<Void, Void> {
    @Override
    public Instructions<Void, Void> getInstruction() {
        return DIE;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Void, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);

        sliceBoard.publishMessage(new Message<>(DEATHS, handle, dataStorage.getMessageLifetime()));
        return true;
    }
}
