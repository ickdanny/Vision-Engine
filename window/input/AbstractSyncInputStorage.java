package window.input;

abstract class AbstractSyncInputStorage implements AbstractInputStorage{
    final synchronized void changeInput(int inputID, boolean inputValue){
        syncChangeInput(inputID, inputValue);
    }
    final synchronized void newTurn(){
        syncNewTurn();
    }
    public final synchronized boolean matchesPattern(int inputID, boolean[] pattern){
        return syncMatchesPattern(inputID, pattern);
    }

    protected abstract void syncChangeInput(int inputID, boolean inputValue);
    protected abstract void syncNewTurn();
    protected abstract boolean syncMatchesPattern(int inputID, boolean[] pattern);
}
