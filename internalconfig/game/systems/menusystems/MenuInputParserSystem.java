package internalconfig.game.systems.menusystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSingleInstanceSystem;
import internalconfig.game.FixedSizeActionTable;
import internalconfig.InputValues;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.InputValues.*;
import static internalconfig.ActionStates.*;
import static internalconfig.game.systems.Topics.*;
import static internalconfig.game.systems.menusystems.MenuNavigationCommands.*;

public class MenuInputParserSystem extends AbstractSingleInstanceSystem<Double> {
    private final FixedSizeActionTable actionTable;

    public MenuInputParserSystem(FixedSizeActionTable actionTable) {
        this.actionTable = actionTable;
    }

    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        parseInputs(ecsInterface);
        lockAllInputs(); //for now assume menus all have zero input transparency
        ecsInterface.getSliceBoard().ageAndCullMessages();
    }

    private void parseInputs(AbstractECSInterface ecsInterface){
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        int messageLifetime = ecsInterface.getSliceData().getMessageLifetime();

        if(isJustPressed(ESC) || isJustPressed(X)){
            publishMessage(sliceBoard, BACK, messageLifetime);
        }
        if(isJustPressed(Z)){
            publishMessage(sliceBoard, SELECT, messageLifetime);
        }
        if(isJustPressed(InputValues.UP)){
            publishMessage(sliceBoard, MenuNavigationCommands.UP, messageLifetime);
        }
        if(isJustPressed(InputValues.DOWN)){
            publishMessage(sliceBoard, MenuNavigationCommands.DOWN, messageLifetime);
        }
        if(isJustPressed(InputValues.LEFT)){
            publishMessage(sliceBoard, MenuNavigationCommands.LEFT, messageLifetime);
        }
        if(isJustPressed(InputValues.RIGHT)){
            publishMessage(sliceBoard, MenuNavigationCommands.RIGHT, messageLifetime);
        }
    }

    private boolean isJustPressed(InputValues inputValue){
        int index = inputValue.getIndex();
        if(actionTable.isNotLocked(index)){
            return actionTable.getActionState(index) == NEW_ACTION;
        }
        return false;
    }

    private void publishMessage(AbstractPublishSubscribeBoard sliceBoard,
                                MenuNavigationCommands navCommand,
                                int messageLifetime){
        sliceBoard.publishMessage(new Message<>(MENU_NAVIGATION_COMMANDS, navCommand, messageLifetime));
    }

    private void lockAllInputs(){
        for(int i = 0; i < actionTable.size(); ++i){
            actionTable.setReadLock(i);
        }
    }
}