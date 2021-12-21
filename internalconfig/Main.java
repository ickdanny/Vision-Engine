package internalconfig;

import internalconfig.game.GameController;
import gameloop.FixedAndInterpTimeLoop;
import resource.Resource;
import resource.ResourceController;
import sound.midi.MusicController;
import util.observer.AbstractObserver;
import window.WindowController;

import java.util.Properties;

import static internalconfig.ResourceTypes.PROPERTIES;
import static internalconfig.InternalProperties.*;
import static internalconfig.MainConfig.*;

@SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
final class Main {

    private static Thread setupThread;

    private static ResourceController resourceController;
    private static WindowController windowController;
    private static MusicController musicController;
    private static GameController gameController;
    private static FixedAndInterpTimeLoop gameLoop;

    private static AbstractObserver cleanupReceiver;

    public static void main(String[] args) {
        setupThread = new Thread("setup") {
            @Override
            public void run() {
                setupThread = Thread.currentThread();
                setup();
                setupThread = null;
            }
        };
        setupThread.start();
    }

    private static void setup() {
        System.setProperty("sun.java2d.d3d", "false");
        makeCleanupReceiver();
        makeResourceController();
        Properties properties = new PropertiesInitializer(resourceController.getResourceManager(PROPERTIES)).init();
        if (Thread.interrupted()) {
            cleanUp();
            return;
        }
        makeMusicController(PropertiesUtil.getBooleanProperty(properties, MUTE.getPropertyName()));
        if (Thread.interrupted()) {
            cleanUp();
            return;
        }
        makeWindowController(PropertiesUtil.getBooleanProperty(properties, FULLSCREEN.getPropertyName()));
        if (Thread.interrupted()) {
            cleanUp();
            return;
        }

        makeGameController();
        if (Thread.interrupted()) {
            cleanUp();
            return;
        }
        makeAndRunGameLoop();
    }

    private static void makeCleanupReceiver() {
        cleanupReceiver = new AbstractObserver() {
            @Override
            public void update() {
                cleanUp();
            }
        };
    }

    private static void makeResourceController() {
        resourceController = ResourceController.makeResourceController(ResourceTypes.values());
        resourceController.loadFile(PROPERTIES_FILE);
        resourceController.loadFile(MANIFEST_FILE);
    }

    private static void makeWindowController(boolean fullscreen) {
        windowController =
                fullscreen
                ? WindowController.makeFullscreenWindowController(
                    WIDTH, HEIGHT, TITLE, InputValues.values(), ActionStates.getMaxTurns()
                )
                : WindowController.makeWindowedWindowController(
                    WIDTH, HEIGHT, TITLE, InputValues.values(), ActionStates.getMaxTurns()
                );
        windowController.getWindowCloseBroadcaster().attach(cleanupReceiver);
    }

    private static void makeMusicController(boolean muted) {
        musicController = MusicController.makeDefaultMusicController(muted);
    }

    private static void makeGameController() {
        gameController = GameController.makeDefaultGameController(resourceController, windowController);

        gameController.getInputConverterBroadcaster().attach(windowController.getInputConverterReceiver());

        gameController.getImageBroadcaster().attach(windowController.getImageReceiver());
        gameController.getImageUpdateBroadcaster().attach(windowController.getImageUpdateReceiver());

        gameController.getCleanupBroadcaster().attach(cleanupReceiver);
        gameController.getMuteToggleBroadcaster().attach(musicController.getMuteToggleReceiver());
        gameController.getFullscreenToggleBroadcaster().attach(windowController.getFullscreenToggleReceiver());
        gameController.getTrackStartBroadcaster().attach(musicController.getTrackStartReceiver());
        gameController.getSequencerResetBroadcaster().attach(musicController.getSequencerResetReceiver());
    }

    private static void makeAndRunGameLoop() {
        gameLoop = new FixedAndInterpTimeLoop(UPDATES_PER_SECOND, MAX_UPDATES_WITHOUT_FRAME);
        gameLoop.getFixedTimeBroadcaster().attach(gameController.getFixedTimeUpdater());
        gameLoop.getInterpTimeBroadcaster().attach(gameController.getInterpTimeUpdater());
        gameLoop.begin();
    }

    private static void cleanUp() {
        if (setupThread != null) {
            setupThread.interrupt();
        }

        Resource<Properties> propertiesResource = resourceController.getResourceManager(PROPERTIES).getResource("properties");
        propertiesResource.writeData();

        windowController = null;
        gameController = null;

        if (gameLoop != null) {
            gameLoop.end();
            gameLoop = null;
        }
        if (musicController != null) {
            musicController.cleanUp();
            musicController = null;
        }
        if (resourceController != null) {
            resourceController.cleanUp();
            resourceController = null;
        }
        System.exit(0);
    }
}
