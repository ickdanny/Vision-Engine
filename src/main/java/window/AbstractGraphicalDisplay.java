package window;

import util.observer.AbstractObserver;
import util.observer.AbstractPushObserver;

import java.awt.image.BufferedImage;

public interface AbstractGraphicalDisplay {
    AbstractPushObserver<BufferedImage> getImageReceiver();
    AbstractObserver getImageUpdateReceiver();
}