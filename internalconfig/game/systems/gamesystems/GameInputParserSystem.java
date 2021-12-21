package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSingleInstanceSystem;
import internalconfig.ActionStates;
import internalconfig.InputValues;
import internalconfig.game.FixedSizeActionTable;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.ActionStates.CONTINUING_ACTION;
import static internalconfig.ActionStates.NEW_ACTION;
import static internalconfig.InputValues.*;
import static internalconfig.game.systems.Topics.GAME_COMMANDS;
import static internalconfig.game.systems.gamesystems.GameCommands.*;

public class GameInputParserSystem extends AbstractSingleInstanceSystem<Double> {
    private final FixedSizeActionTable actionTable;

    public GameInputParserSystem(FixedSizeActionTable actionTable) {
        this.actionTable = actionTable;
    }

    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        parseInputs(ecsInterface);
        lockAllInputs(); //game has zero input transparency
        ecsInterface.getSliceBoard().ageAndCullMessages();
    }

    private void parseInputs(AbstractECSInterface ecsInterface){
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        int messageLifetime = ecsInterface.getSliceData().getMessageLifetime();

        if(isJustPressed(ESC)){
            publishMessage(sliceBoard, PAUSE, messageLifetime);
        }
        if(isBeingPressed(SHIFT)){
            publishMessage(sliceBoard, FOCUS, messageLifetime);
        }
        if(isBeingPressed(Z)){
            publishMessage(sliceBoard, SHOOT, messageLifetime);
        }
        if(isJustPressed(X)){
            publishMessage(sliceBoard, BOMB, messageLifetime);
        }
        if(isBeingPressed(InputValues.UP)){
            publishMessage(sliceBoard, GameCommands.UP, messageLifetime);
        }
        if(isBeingPressed(InputValues.DOWN)){
            publishMessage(sliceBoard, GameCommands.DOWN, messageLifetime);
        }
        if(isBeingPressed(InputValues.LEFT)){
            publishMessage(sliceBoard, GameCommands.LEFT, messageLifetime);
        }
        if(isBeingPressed(InputValues.RIGHT)){
            publishMessage(sliceBoard, GameCommands.RIGHT, messageLifetime);
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
                                GameCommands gameCommand,
                                int messageLifetime){
        sliceBoard.publishMessage(new Message<>(GAME_COMMANDS, gameCommand, messageLifetime));
    }

    private void lockAllInputs(){
        for(int i = 0; i < actionTable.size(); ++i){
            actionTable.setReadLock(i);
        }
    }
}