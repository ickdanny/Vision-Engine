package window;

import util.observer.AbstractObserver;
import util.observer.AbstractPushObserver;
import util.observer.AbstractSubject;
import window.frame.AbstractFrame;
import window.frame.Frame;
import window.input.AbstractInputConverter;
import window.input.AbstractInputValue;
import window.input.InputController;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class WindowController {
    private final AbstractGraphicalDisplay graphicalDisplay;
    private final InputController inputController;
    private final AbstractFrame frame;

    private WindowController(int width,
                             int height,
                             String title,
                             AbstractInputValue[] inputValueArray,
                             int numTurns,
                             Frame.DisplayStates initDisplayState){
        graphicalDisplay = new BufferedImagePanel(width, height);
        inputController = InputController.makeJComponentInputController(
                (JComponent)graphicalDisplay, inputValueArray, numTurns);
        frame = new Frame(width, height, title, (JComponent)graphicalDisplay, initDisplayState);
    }

    public static WindowController makeWindowedWindowController(int width,
                                                                int height,
                                                                String title,
                                                                AbstractInputValue[] inputValueArray,
                                                                int numTurns) {
        return new WindowController(width, height, title, inputValueArray, numTurns, Frame.DisplayStates.WINDOWED);
    }

    public static WindowController makeFullscreenWindowController(int width,
                                                                int height,
                                                                String title,
                                                                AbstractInputValue[] inputValueArray,
                                                                int numTurns) {
        return new WindowController(width, height, title, inputValueArray, numTurns, Frame.DisplayStates.FULLSCREEN);
    }

    public AbstractPushObserver<BufferedImage> getImageReceiver(){
        return graphicalDisplay.getImageReceiver();
    }
    public AbstractObserver getImageUpdateReceiver(){
        return graphicalDisplay.getImageUpdateReceiver();
    }
    public AbstractPushObserver<AbstractInputConverter> getInputConverterReceiver() {
        return inputController.getInputConverterReceiver();
    }
    public AbstractSubject getWindowCloseBroadcaster(){
        return frame.getWindowCloseBroadcaster();
    }
    public AbstractObserver getFullscreenToggleReceiver(){
        return frame.getFullscreenToggleReceiver();
    }
}