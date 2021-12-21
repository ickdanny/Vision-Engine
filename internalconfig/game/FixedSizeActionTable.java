package internalconfig.game;

import internalconfig.ActionStates;

import java.util.Arrays;

public class FixedSizeActionTable {
    private final ActionStates[] actionTable;
    private final boolean[] readLocks;

    public FixedSizeActionTable(int numInputs){
        actionTable = new ActionStates[numInputs];
        readLocks = new boolean[numInputs];
        initActionTable();
        initReadLocks();
    }
    private void initActionTable(){
        Arrays.fill(actionTable, ActionStates.CONTINUING_INACTION);
    }
    private void initReadLocks(){
        resetReadLocks();
    }

    public void setReadLock(int inputID){
        readLocks[inputID] = true;
    }
    public void resetReadLocks(){
        Arrays.fill(readLocks, false);
    }
    public boolean isNotLocked(int inputID){
        return !isLocked(inputID);
    }
    private boolean isLocked(int inputID){
        return readLocks[inputID];
    }

    public ActionStates getActionState(int inputID){
        if(isLocked(inputID)){
            throw new RuntimeException("Cannot access locked ActionState " + inputID);
        }
        return actionTable[inputID];
    }
    public void setActionState(int inputID, ActionStates state){
        actionTable[inputID] = state;
    }

    public int size() {
        return actionTable.length;
    }
}