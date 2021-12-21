package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import util.messaging.Message;

import static internalconfig.game.systems.Topics.*;

class StartTrackInstructionHandler implements AbstractInstructionHandler<String, Void> {
    @Override
    public Instructions<String, Void> getInstruction() {
        return Instructions.START_TRACK;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<String, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        String trackID = node.getData();
        ecsInterface.getSliceBoard().publishMessage(new Message<>(MUSIC, trackID, Message.AGELESS));
        return true;
    }
}
