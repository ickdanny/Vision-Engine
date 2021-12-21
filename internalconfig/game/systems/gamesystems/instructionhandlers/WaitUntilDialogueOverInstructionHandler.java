package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GlobalTopics.DIALOGUE_OVER;

class WaitUntilDialogueOverInstructionHandler implements AbstractInstructionHandler<Void, Void>{

    @Override
    public Instructions<Void, Void> getInstruction() {
        return Instructions.WAIT_UNTIL_DIALOGUE_OVER;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Void, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
        if(globalBoard.hasTopicalMessages(DIALOGUE_OVER)){
            globalBoard.getMessageList(DIALOGUE_OVER).clear();
            return true;
        }
        return false;
    }
}
