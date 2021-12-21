package window.input;

import util.observer.AbstractPushObserver;

interface AbstractInputParser {
    AbstractPushObserver<Boolean> getInputReceiver(AbstractInputValue inputValue);
    AbstractPushObserver<AbstractInputConverter> getInputConverterReceiver();
}
//may be a better mechanism for window.input-action conversion but this decouples the two sides nicely
//-albeit probably an improper usage of observers