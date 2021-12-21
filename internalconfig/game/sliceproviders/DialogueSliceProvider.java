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
import ecs.entity.NamedEntityHandle;
import ecs.system.SystemChainInfo;
import ecs.system.criticalorders.AddEntityOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.DrawOrder;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.SpriteInstruction;
import util.math.geometry.DoublePoint;
import util.math.geometry.TwoFramePosition;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static ecs.ECSTopics.NEW_NAMED_ENTITIES;
import static internalconfig.game.SystemChainCalls.*;
import static internalconfig.game.components.ComponentTypes.DIALOGUE_COMPONENT_TYPES;
import static internalconfig.game.systems.SliceCodes.DIALOGUE;
import static internalconfig.game.systems.Topics.DIALOGUE_TOPICS;
import static internalconfig.game.systems.Topics.LOWER_IMAGE_HANDLE;
import static internalconfig.game.systems.Topics.LOWER_TEXT_HANDLE;
import static internalconfig.game.systems.Topics.READ_DIALOGUE_COMMAND;
import static internalconfig.game.systems.Topics.UPPER_IMAGE_HANDLE;
import static internalconfig.game.systems.Topics.UPPER_TEXT_HANDLE;

@SuppressWarnings("unused")
public class DialogueSliceProvider extends SliceProvider {
    public DialogueSliceProvider(SystemChainFactoryProvider systemChainFactoryProvider) {
        super(DIALOGUE, true, DIALOGUE_TOPICS,
                makeDataStorageConfig(systemChainFactoryProvider),
                makeSystemChainInfo(systemChainFactoryProvider),
                new DialogueInitScript(DIALOGUE_COMPONENT_TYPES));
    }

    private static AbstractDataStorageConfig makeDataStorageConfig(
            SystemChainFactoryProvider systemChainFactoryProvider) {

        int numSystems = systemChainFactoryProvider.getDialogueMainSystemChainFactory().getNumSystems();
        return new DataStorageConfig(
                DIALOGUE_COMPONENT_TYPES.getArray(), true, 4, numSystems);
    }

    @SuppressWarnings("unchecked")
    private static SystemChainInfo<Double>[] makeSystemChainInfo(SystemChainFactoryProvider systemChainFactoryProvider) {
        SystemChainInfo<Double> mainInfo = new SystemChainInfo<>(
                MAIN, systemChainFactoryProvider.getDialogueMainSystemChainFactory(), true);
        SystemChainInfo<Double> graphicsInfo = new SystemChainInfo<>(
                GRAPHICS, systemChainFactoryProvider.getDialogueGraphicsSystemChainFactory(), true);
        return (SystemChainInfo<Double>[]) new SystemChainInfo[]{mainInfo, graphicsInfo};
    }

    private static class DialogueInitScript extends AbstractSliceInitScript {

        private static final String LOWER_IMAGE = "lower_image";
        private static final String UPPER_IMAGE = "upper_image";
        private static final String LOWER_TEXT = "lower_text";
        private static final String UPPER_TEXT = "upper_text";

        private static final DoublePoint LOWER_IMAGE_POS = new DoublePoint(20, 350);
        private static final DoublePoint UPPER_IMAGE_POS = new DoublePoint(20, 120);
        private static final DoublePoint LOWER_TEXT_POS = new DoublePoint(174, 511);
        private static final DoublePoint UPPER_TEXT_POS = new DoublePoint(21, 281);

        protected final AbstractComponentType<TwoFramePosition> positionComponentType;
        protected final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
        protected final AbstractComponentType<DrawOrder> drawOrderComponentType;
        protected final AbstractComponentType<Void> visibleMarker;

        public DialogueInitScript(AbstractComponentTypeContainer componentTypeContainer) {
            positionComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.PositionComponentType.class);
            spriteInstructionComponentType =
                    componentTypeContainer.getTypeInstance(ComponentTypes.SpriteInstructionComponentType.class);
            drawOrderComponentType =
                    componentTypeContainer.getTypeInstance(ComponentTypes.DrawOrderComponentType.class);
            visibleMarker = componentTypeContainer.getTypeInstance(ComponentTypes.VisibleMarker.class);
        }

        @Override
        public void runOn(AbstractECSInterface ecsInterface) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            publishMessages(sliceBoard);
            carryOutCriticalOrders(dataStorage);
            publishHandles(sliceBoard);
            sliceBoard.publishMessage(new Message<>(READ_DIALOGUE_COMMAND, null, dataStorage.getMessageLifetime()));
        }

        private void publishMessages(AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(makeVisibleEntityMessage(LOWER_IMAGE, LOWER_IMAGE_POS, 0));
            sliceBoard.publishMessage(makeVisibleEntityMessage(UPPER_IMAGE, UPPER_IMAGE_POS, 0));
            sliceBoard.publishMessage(makeVisibleEntityMessage(LOWER_TEXT, LOWER_TEXT_POS, 1));
            sliceBoard.publishMessage(makeVisibleEntityMessage(UPPER_TEXT, UPPER_TEXT_POS, 1));
        }

        protected Message<AddEntityOrder> makeVisibleEntityMessage(String name, DoublePoint pos, int drawOrder) {
            return ECSUtil.makeAddEntityMessage(new AddEntityOrder(new TypeComponentTuple[]{
                    new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(pos)),
                    new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.BACKGROUND, drawOrder)),
                    new TypeComponentTuple<>(visibleMarker),
            }, name));
        }

        private void publishHandles(AbstractPublishSubscribeBoard sliceBoard){
            if (!sliceBoard.hasTopicalMessages(NEW_NAMED_ENTITIES)) {
                throw new RuntimeException("cannot find any named entities");
            }
            List<Message<NamedEntityHandle>> newNamedEntities = sliceBoard.getMessageList(NEW_NAMED_ENTITIES);
            for (Message<NamedEntityHandle> message : newNamedEntities) {
                NamedEntityHandle handle = message.getMessage();
                String name = handle.getName();
                switch(name){
                    case LOWER_IMAGE:
                        sliceBoard.publishMessage(new Message<>(LOWER_IMAGE_HANDLE, handle, Message.AGELESS));
                        break;
                    case UPPER_IMAGE:
                        sliceBoard.publishMessage(new Message<>(UPPER_IMAGE_HANDLE, handle, Message.AGELESS));
                        break;
                    case LOWER_TEXT:
                        sliceBoard.publishMessage(new Message<>(LOWER_TEXT_HANDLE, handle, Message.AGELESS));
                        break;
                    case UPPER_TEXT:
                        sliceBoard.publishMessage(new Message<>(UPPER_TEXT_HANDLE, handle, Message.AGELESS));
                        break;
                    default:
                        throw new RuntimeException("unrecognized named entity: " + name);
                }
            }
        }
    }
}