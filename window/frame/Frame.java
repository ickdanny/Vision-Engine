package window.frame;

import util.observer.AbstractObserver;

import javax.swing.*;
import java.awt.*;

public class Frame extends AbstractFrame {

    private final int windowedWidth;
    private final int windowedHeight;
    private final String title;
    private final JComponent content;

    private DisplayStates currentState;

    private final AbstractObserver fullscreenToggleReceiver;

    public Frame(int width, int height, String title, JComponent component, DisplayStates initState){
        super();

        windowedWidth = width;
        windowedHeight = height;
        this.title = title;
        content = component;
        currentState = initState;

        fullscreenToggleReceiver = makeFullscreenToggleReceiver();

        init();
    }

    private AbstractObserver makeFullscreenToggleReceiver(){
        return this::toggleState;
    }

    private void toggleState(){
        if(currentState == DisplayStates.WINDOWED){
            setState(DisplayStates.FULLSCREEN);
        }
        else{
            setState(DisplayStates.WINDOWED);
        }
    }

    private void init(){
        setTitle(title);
        setResizable(false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(content, BorderLayout.CENTER);

        currentState.onEntry(this);

        setVisible(true);
    }

    private void setState(DisplayStates newState){
        if(newState != currentState){
            currentState.onExit(this);
            currentState = newState;
            currentState.onEntry(this);
        }
    }

    @Override
    public AbstractObserver getFullscreenToggleReceiver() {
        return fullscreenToggleReceiver;
    }

    public enum DisplayStates{
        WINDOWED(){
            @Override
            protected void onEntry(Frame frame) {
                frame.content.setPreferredSize(new Dimension(frame.windowedWidth, frame.windowedHeight));
                frame.setUndecorated(false);
                frame.pack();
                super.onEntry(frame);
            }
        },
        FULLSCREEN(){
            @Override
            protected void onEntry(Frame frame){
                GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();

                if(graphicsDevice.isFullScreenSupported()){
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    frame.content.setPreferredSize(screenSize);
                    frame.setUndecorated(true);
                    frame.pack();

                    graphicsDevice.setFullScreenWindow(frame);
                    super.onEntry(frame);
                }
                else{
                    frame.setState(FULLSCREEN_WINDOWED);
                }
            }

            @Override
            protected void onExit(Frame frame) {
                super.onExit(frame);
                GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
                graphicsDevice.setFullScreenWindow(null);
            }
        },
        FULLSCREEN_WINDOWED(){
            @Override
            protected void onEntry(Frame frame) {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                frame.content.setPreferredSize(screenSize);
                frame.setUndecorated(true);
                frame.pack();
                super.onEntry(frame);
            }
        }
        ;
        protected void onEntry(Frame frame){
            frame.setVisible(true);
        }
        protected void onExit(Frame frame){
            frame.windowCloseBroadcaster.block();
            frame.dispose();
        }
    }
}