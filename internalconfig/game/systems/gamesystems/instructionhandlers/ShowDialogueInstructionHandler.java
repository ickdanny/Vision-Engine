package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.game.components.Instructions.SHOW_DIALOGUE;
import static internalconfig.game.systems.Topics.DIALOGUE_ENTRY;

class ShowDialogueInstructionHandler implements AbstractInstructionHandler<String, Void>{

    @Override
    public Instructions<String, Void> getInstruction() {
        return SHOW_DIALOGUE;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<String, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {

        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        String dialogueCode = node.getData();

        sliceBoard.publishMessage(new Message<>(DIALOGUE_ENTRY, dialogueCode, dataStorage.getMessageLifetime()));
        return true;
    }
}
