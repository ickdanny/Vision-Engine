package window.input;

import util.observer.AbstractPushSubject;

interface AbstractKeyOutputter {
    AbstractPushSubject<Boolean> getKeyBroadcaster(KeyValues key);
}
