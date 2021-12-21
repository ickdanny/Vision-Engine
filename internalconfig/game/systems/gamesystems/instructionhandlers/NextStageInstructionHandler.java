package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.game.components.Instructions.NEXT_STAGE;
import static internalconfig.game.systems.Topics.NEXT_STAGE_ENTRY;

class NextStageInstructionHandler implements AbstractInstructionHandler<Void, Void>{

    @Override
    public Instructions<Void, Void> getInstruction() {
        return NEXT_STAGE;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Void, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {

        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        sliceBoard.publishMessage(new Message<>(NEXT_STAGE_ENTRY, null, Message.AGELESS));
        return true;
    }
}
