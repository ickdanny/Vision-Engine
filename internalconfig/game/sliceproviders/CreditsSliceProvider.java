package internalconfig.game.sliceproviders;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.SliceProvider;
import ecs.component.AbstractComponentType;
import ecs.component.TypeComponentTuple;
import ecs.datastorage.AbstractDataStorage;
import ecs.datastorage.AbstractDataStorageConfig;
import ecs.datastorage.AbstractSliceInitScript;
import ecs.datastorage.DataStorageConfig;
import ecs.system.SystemChainInfo;
import ecs.system.criticalorders.AddEntityOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.DrawOrder;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.systems.SystemContainer;
import util.math.geometry.TwoFramePosition;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.game.SystemChainCalls.GRAPHICS;
import static internalconfig.game.SystemChainCalls.MAIN;
import static internalconfig.game.systems.Topics.MUSIC;
import static internalconfig.game.systems.Topics.SHARED_TOPICS;
import static internalconfig.game.systems.SliceCodes.CREDITS;
import static internalconfig.game.components.ComponentTypes.CREDITS_COMPONENT_TYPES;

public class CreditsSliceProvider extends SliceProvider {

    public CreditsSliceProvider(SystemChainFactoryProvider systemChainFactoryProvider,
                                SystemContainer systemContainer) {
        super(
                CREDITS,
                true,
                SHARED_TOPICS,
                makeDataStorageConfig(systemChainFactoryProvider),
                makeSystemChainInfo(systemChainFactoryProvider),
                new CreditsInitScript(CREDITS_COMPONENT_TYPES, systemContainer)
        );
    }

    private static AbstractDataStorageConfig makeDataStorageConfig(SystemChainFactoryProvider systemChainFactoryProvider) {

        int numSystems = systemChainFactoryProvider.getCreditsMainSystemChainFactory().getNumSystems();
        return new DataStorageConfig(
                CREDITS_COMPONENT_TYPES.getArray(), true, 1, numSystems);
    }

    @SuppressWarnings("unchecked")
    private static SystemChainInfo<Double>[] makeSystemChainInfo(SystemChainFactoryProvider systemChainFactoryProvider) {
        SystemChainInfo<Double> mainInfo = new SystemChainInfo<>(
                MAIN, systemChainFactoryProvider.getCreditsMainSystemChainFactory(), false);

        SystemChainInfo<Double> graphicsInfo = new SystemChainInfo<>(
                GRAPHICS, systemChainFactoryProvider.getCreditsGraphicsSystemChainFactory(), false);

        return (SystemChainInfo<Double>[]) new SystemChainInfo[]{mainInfo, graphicsInfo};
    }

    private static class CreditsInitScript extends AbstractSliceInitScript {

        protected final AbstractComponentType<TwoFramePosition> positionComponentType;
        protected final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
        protected final AbstractComponentType<DrawOrder> drawOrderComponentType;
        protected final AbstractComponentType<Void> visibleMarker;

        private final SystemContainer systemContainer;

        public CreditsInitScript(AbstractComponentTypeContainer componentTypeContainer,
                                 SystemContainer systemContainer) {
            positionComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.PositionComponentType.class);
            spriteInstructionComponentType =
                    componentTypeContainer.getTypeInstance(ComponentTypes.SpriteInstructionComponentType.class);
            drawOrderComponentType =
                    componentTypeContainer.getTypeInstance(ComponentTypes.DrawOrderComponentType.class);
            visibleMarker = componentTypeContainer.getTypeInstance(ComponentTypes.VisibleMarker.class);

            this.systemContainer = systemContainer;
        }

        @Override
        public void runOn(AbstractECSInterface ecsInterface) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            publishMessages(sliceBoard);
            carryOutCriticalOrders(dataStorage);
            systemContainer.getCreditsSpriteInstructionSystem().makeInstance().run(ecsInterface, 0d);
            carryOutCriticalOrders(dataStorage);
            systemContainer.getCreditsDrawCommandSystem().makeInstance().run(ecsInterface, 0d);

            sliceBoard.publishMessage(new Message<>(MUSIC, "16", Message.AGELESS));
        }

        private void publishMessages(AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    ECSUtil.makeAddEntityMessage(
                            new AddEntityOrder(new TypeComponentTuple[]{
                                    new TypeComponentTuple<>(positionComponentType, new TwoFramePosition()),
                                    new TypeComponentTuple<>(spriteInstructionComponentType, new SpriteInstruction("background_credits")),
                                    new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.BACKGROUND, 0)),
                                    new TypeComponentTuple<>(visibleMarker),
                            })
                    )
            );
        }
    }
}