package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.systems.gamesystems.PlayerStateSystem;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static internalconfig.game.systems.Topics.PLAYER_STATE_ENTRY;

abstract class AbstractWaitPlayerStateInstructionHandler implements AbstractInstructionHandler<Void, Void> {

    private final PlayerStateSystem.States stateToWaitFor;

    AbstractWaitPlayerStateInstructionHandler(PlayerStateSystem.States stateToWaitFor) {
        this.stateToWaitFor = stateToWaitFor;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Void, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        if (sliceBoard.hasTopicalMessages(PLAYER_STATE_ENTRY)) {
            List<Message<PlayerStateSystem.States>> list = sliceBoard.getMessageList(PLAYER_STATE_ENTRY);
            for (Message<PlayerStateSystem.States> message : list) {
                if (message.getMessage() == stateToWaitFor) {
                    return true;
                }
            }
        }
        return false;
    }
}
