package internalconfig.game.systems.dialoguesystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSingleInstanceSystem;
import internalconfig.ActionStates;
import internalconfig.InputValues;
import internalconfig.game.FixedSizeActionTable;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.ActionStates.*;
import static internalconfig.InputValues.*;
import static internalconfig.game.systems.Topics.READ_DIALOGUE_COMMAND;

@SuppressWarnings("SameParameterValue")
public class DialogueInputParserSystem extends AbstractSingleInstanceSystem<Double> {
    private final FixedSizeActionTable actionTable;

    public DialogueInputParserSystem(FixedSizeActionTable actionTable) {
        this.actionTable = actionTable;
    }

    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        parseInputs(ecsInterface);
        lockInputs();
        ecsInterface.getSliceBoard().ageAndCullMessages();
    }

    private void parseInputs(AbstractECSInterface ecsInterface){
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        int messageLifetime = ecsInterface.getSliceData().getMessageLifetime();

        if(isJustPressed(Z) || isBeingPressed(CTRL)){
            publishMessage(sliceBoard, messageLifetime);
        }
    }

    private boolean isJustPressed(InputValues inputValue){
        int index = inputValue.getIndex();
        if(actionTable.isNotLocked(index)){
            return actionTable.getActionState(index) == NEW_ACTION;
        }
        return false;
    }

    private boolean isBeingPressed(InputValues inputValue){
        int index = inputValue.getIndex();
        if(actionTable.isNotLocked(index)){
            ActionStates actionState = actionTable.getActionState(index);
            return actionState == NEW_ACTION || actionState == CONTINUING_ACTION;
        }
        return false;
    }

    private void publishMessage(AbstractPublishSubscribeBoard sliceBoard,
                                int messageLifetime){
        sliceBoard.publishMessage(new Message<>(READ_DIALOGUE_COMMAND, null, messageLifetime));
    }

    private void lockInputs(){
        actionTable.setReadLock(Z.getIndex());
        actionTable.setReadLock(X.getIndex());
//        actionTable.setReadLock(ESC.getIndex());
        actionTable.setReadLock(CTRL.getIndex());
    }
}