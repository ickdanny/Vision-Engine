package internalconfig.game;

import ecs.ECS;
import internalconfig.InputValues;
import internalconfig.game.sliceproviders.SliceProviderContainer;
import internalconfig.game.sliceproviders.SystemChainFactoryProvider;
import internalconfig.game.systems.SystemContainer;
import resource.Resource;
import resource.ResourceController;
import sound.midi.MidiSequence;
import util.image.ImageUtil;
import util.observer.AbstractPushObserver;
import util.observer.AbstractPushSubject;
import util.observer.AbstractSubject;
import util.observer.ConfigurablePushSubject;
import util.observer.PushSubjectTemplate;
import util.observer.Subject;
import window.WindowController;
import window.input.AbstractInputConverter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Properties;

import static internalconfig.MainConfig.*;
import static internalconfig.game.SystemChainCalls.*;
import static internalconfig.ResourceTypes.*;

@SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
public class GameController {
    private final AbstractPushObserver<Double> fixedTimeUpdater;
    private final AbstractPushObserver<Double> interpTimeUpdater;

    private final FixedSizeActionTable actionTable;
    private final AbstractPushSubject<AbstractInputConverter> inputConverterBroadcaster;

    private final BufferedImage toDraw;
    private final ConfigurablePushSubject<BufferedImage> imageBroadcaster;
    private final AbstractSubject imageUpdateBroadcaster;

    private final SystemContainer systemContainer;
    @SuppressWarnings("FieldCanBeLocal")
    private final SystemChainFactoryProvider systemChainFactoryProvider;
    @SuppressWarnings("FieldCanBeLocal")
    private final SliceProviderContainer sliceProviderContainer;
    private final ECS ecs;

    private GameController(ResourceController resourceController, WindowController windowController){
        fixedTimeUpdater = makeFixedTimeUpdater();
        interpTimeUpdater = makeInterpTimeUpdater();
        actionTable = new FixedSizeActionTable(InputValues.values().length);
        inputConverterBroadcaster = makeInputConverterBroadcaster();

        toDraw = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        hookToDrawToWindowController(windowController);
        imageBroadcaster = new ConfigurablePushSubject<>();
        imageUpdateBroadcaster = new Subject();

        Resource<Properties> propertiesResource = resourceController.getResourceManager(PROPERTIES).getResource("properties");
        Properties properties = propertiesResource.getData();

        systemContainer = new SystemContainer(
                propertiesResource,
                resourceController.getResourceManager(IMAGE),
                resourceController.getResourceManager(MIDI_SEQUENCE),
                resourceController.getResourceManager(DIALOGUE),
                actionTable,
                toDraw
        );
        systemChainFactoryProvider = new SystemChainFactoryProvider(systemContainer);
        sliceProviderContainer = new SliceProviderContainer(properties, systemChainFactoryProvider, systemContainer, resourceController.getResourceManager(IMAGE));
        ecs = new ECS(new ECSConfigObject(sliceProviderContainer));
    }

    private AbstractPushSubject<AbstractInputConverter> makeInputConverterBroadcaster(){
        return new PushSubjectTemplate<AbstractInputConverter>() {
            private final AbstractInputConverter inputConverter = new InputConverter(actionTable);

            @Override
            protected AbstractInputConverter getPushData() {
                return inputConverter;
            }
        };
    }

    private void hookToDrawToWindowController(WindowController windowController){
        AbstractPushSubject<BufferedImage> pushSubject = new ConfigurablePushSubject<>(toDraw);
        pushSubject.attach(windowController.getImageReceiver());
        pushSubject.broadcast();
    }

    public static GameController makeDefaultGameController(ResourceController resourceController,
                                                           WindowController windowController){
        return new GameController(resourceController, windowController);
    }

    private AbstractPushObserver<Double> makeFixedTimeUpdater(){
        return new AbstractPushObserver<Double>() {
            @Override
            public void update(Double data) {
                fixedTimeUpdate(data);
            }
        };
    }
    private AbstractPushObserver<Double> makeInterpTimeUpdater(){
        return new AbstractPushObserver<Double>() {
            @Override
            public void update(Double data) {
                interpTimeUpdate(data);
            }
        };
    }

    public AbstractPushObserver<Double> getFixedTimeUpdater() {
        return fixedTimeUpdater;
    }
    public AbstractPushObserver<Double> getInterpTimeUpdater() {
        return interpTimeUpdater;
    }

    public AbstractPushSubject<AbstractInputConverter> getInputConverterBroadcaster() {
        return inputConverterBroadcaster;
    }
    public AbstractPushSubject<BufferedImage> getImageBroadcaster() {
        return imageBroadcaster;
    }

    public AbstractSubject getImageUpdateBroadcaster() {
        return imageUpdateBroadcaster;
    }

    public AbstractSubject getCleanupBroadcaster(){
        return systemContainer.getCleanupBroadcaster();
    }
    public AbstractSubject getFullscreenToggleBroadcaster(){
        return systemContainer.getFullscreenToggleBroadcaster();
    }
    public AbstractPushSubject<MidiSequence> getTrackStartBroadcaster() {
        return systemContainer.getTrackStartBroadcaster();
    }
    public AbstractSubject getSequencerResetBroadcaster() {
        return systemContainer.getSequencerResetBroadcaster();
    }
    public AbstractSubject getMuteToggleBroadcaster(){
        return systemContainer.getMuteToggleBroadcaster();
    }

    private void fixedTimeUpdate(double deltaTime){
        MAIN.setDeltaTime(deltaTime);
        inputConverterBroadcaster.broadcast();
        ecs.receiveSystemChainCall(MAIN);
    }
    private void interpTimeUpdate(double deltaTime){
        ImageUtil.clearImage(toDraw, Color.GREEN);
        GRAPHICS.setDeltaTime(deltaTime);
        ecs.receiveSystemChainCall(GRAPHICS);
        imageBroadcaster.setPushData(ImageUtil.copyImage(toDraw));
        imageBroadcaster.broadcast();
        imageUpdateBroadcaster.broadcast();
    }
}