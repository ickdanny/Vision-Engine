package window.input;

import util.observer.AbstractPushObserver;

class StaticInputBinder implements AbstractInputBinder {
    StaticInputBinder(AbstractKeyOutputter outputter, AbstractInputParser parser, AbstractInputValue[] inputValueArray){
        for(AbstractInputValue inputValue : inputValueArray){
            AbstractPushObserver<Boolean> receiver = parser.getInputReceiver(inputValue);
            outputter.getKeyBroadcaster(inputValue.getKey()).attach(receiver);
        }
    }
}