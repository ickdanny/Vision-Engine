package window.input;

import util.observer.AbstractPushObserver;

@SuppressWarnings("Convert2Lambda")
class FixedSizeInputParser implements AbstractInputParser {
    private final AbstractSyncInputStorage inputStorage;
    private final AbstractPushObserver<Boolean>[] inputReceiverArray;
    private final AbstractPushObserver<AbstractInputConverter> inputToActionConverter;

    FixedSizeInputParser(AbstractInputValue[] inputValueArray, int numTurns){
        inputStorage = makeInputTable(inputValueArray, numTurns);
        inputReceiverArray = makeInputReceiverArray(inputValueArray);
        inputToActionConverter = makeInputToActionConverter();
    }

    private AbstractSyncInputStorage makeInputTable(AbstractInputValue[] inputValueArray, int numTurns){
        return new FixedSizeInputTable(inputValueArray.length, numTurns);
    }

    @SuppressWarnings("unchecked")
    private AbstractPushObserver<Boolean>[] makeInputReceiverArray(AbstractInputValue[] inputValueArray){
        AbstractPushObserver<Boolean>[] inputReceivers =
                (AbstractPushObserver<Boolean>[])new AbstractPushObserver[inputValueArray.length];
        for(int i = 0; i < inputValueArray.length; i++){
            inputReceivers[i] = makeInputReceiver(i);
        }
        return inputReceivers;
    }

    private AbstractPushObserver<Boolean> makeInputReceiver(int inputID){
        return new AbstractPushObserver<Boolean>() {
            @Override
            public void update(Boolean data) {
                inputStorage.changeInput(inputID, data);
            }
        };
    }

    private AbstractPushObserver<AbstractInputConverter> makeInputToActionConverter(){
        return new AbstractPushObserver<AbstractInputConverter>() {
            @Override
            public void update(AbstractInputConverter data) {
                synchronized (inputStorage) {
                    data.convertInput(inputStorage);
                    updateInputTable();
                }
            }

            private void updateInputTable() {
                inputStorage.newTurn();
            }
        };
    }

    @Override
    public AbstractPushObserver<Boolean> getInputReceiver(AbstractInputValue inputValue) {
        return inputReceiverArray[inputValue.getIndex()];
    }

    @Override
    public AbstractPushObserver<AbstractInputConverter> getInputConverterReceiver() {
        return inputToActionConverter;
    }
}