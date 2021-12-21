package internalconfig.game;

import internalconfig.ActionStates;
import internalconfig.InputValues;
import window.input.AbstractInputConverter;
import window.input.AbstractInputStorage;
import window.input.AbstractInputValue;

class InputConverter implements AbstractInputConverter {

    private final FixedSizeActionTable actionTable;

    public InputConverter(FixedSizeActionTable actionTable) {
        this.actionTable = actionTable;
    }

    @Override
    public void convertInput(AbstractInputStorage inputStorage) {
        actionTable.resetReadLocks();
        for(AbstractInputValue value : InputValues.values()){
            int index = value.getIndex();
            actionTable.setActionState(value.getIndex(), getActionState(inputStorage, index));
        }
    }

    private ActionStates getActionState(AbstractInputStorage inputStorage, int index){
        for(ActionStates testState : ActionStates.values()){
            if(inputStorage.matchesPattern(index, testState.pattern)){
                return testState;
            }
        }
        throw new RuntimeException("no matching ActionStates for input " + index);
    }
}
