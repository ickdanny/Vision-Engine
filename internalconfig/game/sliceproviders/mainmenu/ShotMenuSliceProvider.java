package internalconfig.game.sliceproviders.mainmenu;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.component.TypeComponentTuple;
import ecs.entity.EntityHandle;
import ecs.system.criticalorders.AddEntityOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Animation;
import internalconfig.game.components.AnimationComponent;
import internalconfig.game.components.ButtonData;
import internalconfig.game.components.DrawOrder;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.MenuCommands;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.sliceproviders.SystemChainFactoryProvider;
import internalconfig.game.sliceproviders.sliceinitscripts.AbstractVerticalButtonAxisMenuInitScript;
import internalconfig.game.systems.ShotType;
import internalconfig.game.systems.SystemContainer;
import internalconfig.game.systems.menusystems.ShotPreviewSystem;
import util.datastructure.ArrayUtil;
import util.math.geometry.ConstCartesianVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.TwoFramePosition;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.game.components.MenuCommands.*;
import static internalconfig.game.systems.SliceCodes.SHOT;

import static internalconfig.game.components.ComponentTypes.*;

import static internalconfig.game.systems.Topics.*;

@SuppressWarnings("unused")
public class ShotMenuSliceProvider extends AbstractStaticEntityCountMenuSliceProvider {

    public static final DoublePoint PLAYER_POS = new DoublePoint(222, 480);

    private static final int ENTITY_COUNT = 60;

    public ShotMenuSliceProvider(SystemChainFactoryProvider systemChainFactoryProvider,
                                 SystemContainer systemContainer,
                                 String name) {
        super(
                name,
                ENTITY_COUNT,
                systemChainFactoryProvider,
                false,
                new ShotMenuInitScript(MENU_COMPONENT_TYPES, systemContainer)
        );
    }

    @SuppressWarnings("SameParameterValue")
    private static class ShotMenuInitScript extends AbstractVerticalButtonAxisMenuInitScript {

        private final AbstractComponentType<AnimationComponent> animationComponentType;
        private final AbstractComponentType<Double> constantSpriteRotationComponentType;

        public ShotMenuInitScript(AbstractComponentTypeContainer componentTypeContainer,
                                  SystemContainer systemContainer) {
            super(componentTypeContainer,
                    systemContainer.getMenuSpriteInstructionSystem(),
                    systemContainer.getMenuDrawCommandSystem());
            animationComponentType = componentTypeContainer.getTypeInstance(AnimationComponentType.class);
            constantSpriteRotationComponentType = componentTypeContainer.getTypeInstance(ConstantSpriteRotationComponentType.class);
        }

        @Override
        public void runOn(AbstractECSInterface ecsInterface) {
            super.runOn(ecsInterface);
            ecsInterface.getSliceBoard().publishMessage(new Message<>(SHOT_TYPE_SELECTION_TOPIC, ShotType.A, ecsInterface.getSliceData().getMessageLifetime()));
        }

        @Override
        protected void hookTwoButtons(AbstractPublishSubscribeBoard sliceBoard, EntityHandle buttonA, EntityHandle buttonB) {
            sliceBoard.publishMessage(makeAddNeighboringElementDownComponentMessage(buttonA, buttonB));
            sliceBoard.publishMessage(makeAddNeighboringElementUpComponentMessage(buttonA, buttonB));
        }

        @Override
        protected void publishOrders(AbstractPublishSubscribeBoard sliceBoard) {
            super.publishOrders(sliceBoard);
            sliceBoard.publishMessage(makePlayerMessage());
            sliceBoard.publishMessage(makeBulletSlowBarrierMessage());
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddBackgroundMessages() {
            return (Message<AddEntityOrder>[]) new Message[]{
                    makeBackgroundMessage("background_shot_2", new DoublePoint(116, 170),0),
                    makeForegroundMessage("background_shot", 0)
            };
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddButtonMessages() {
            double offset = 2;
            double spacing = 90;
            double x = 544 - 144;
            double y = 190 + offset - 27;
            return (Message<AddEntityOrder>[]) new Message[]{
                    makeAddFirstButtonMessage(
                            "a",
                            ENTER,
                            0,
                            new ButtonData(
                                    new DoublePoint(x, y),
                                    new DoublePoint(x, y - offset),
                                    "button_a"
                            ),
                            SHOT + "A",
                            new TypeComponentTuple<?>[]{
                                    new TypeComponentTuple<>(commandDownComponentType, NAV_DOWN_AND_SET_SHOT_C),
                            }
                    ),
                    makeAddButtonMessage(
                            "c",
                            ENTER,
                            0,
                            new ButtonData(
                                    new DoublePoint(x, y + spacing),
                                    new DoublePoint(x, y + spacing - offset),
                                    "button_c"
                            ),
                            SHOT + "C",
                            new TypeComponentTuple<?>[]{
                                    new TypeComponentTuple<>(commandDownComponentType, NAV_DOWN_AND_SET_SHOT_B),
                                    new TypeComponentTuple<>(commandUpComponentType, NAV_UP_AND_SET_SHOT_A),
                            }
                    ),
                    makeAddButtonMessage(
                            "b",
                            ENTER,
                            0,
                            new ButtonData(
                                    new DoublePoint(x, y + spacing + spacing),
                                    new DoublePoint(x, y + spacing + spacing - offset),
                                    "button_b"
                            ),
                            SHOT + "B",
                            new TypeComponentTuple<?>[]{
                                    new TypeComponentTuple<>(commandUpComponentType, NAV_UP_AND_SET_SHOT_C),
                            }
                    ),
            };
        }

        private Message<AddEntityOrder> makeAddFirstButtonMessage(String name,
                                                                  MenuCommands onSelect,
                                                                  int drawOrder,
                                                                  ButtonData buttonData,
                                                                  String actionCode,
                                                                  TypeComponentTuple<?>[] extraTuples) {
            return ECSUtil.makeAddEntityMessage(new AddEntityOrder(
                    ArrayUtil.addArrays(
                            new TypeComponentTuple[]{
                                    new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(buttonData.getSelPos())),
                                    new TypeComponentTuple<>(commandSelectComponentType, onSelect),
                                    new TypeComponentTuple<>(buttonComponentType, buttonData),
                                    new TypeComponentTuple<>(buttonActionComponentType, actionCode),
                                    new TypeComponentTuple<>(spriteInstructionComponentType,
                                            new SpriteInstruction(buttonData.getSelImage())),
                                    new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.FOREGROUND, drawOrder)),
                                    new TypeComponentTuple<>(defaultSelectedElementMarker),
                                    new TypeComponentTuple<>(visibleMarker),
                            },
                            extraTuples
                    ), name));
        }

        private Message<AddEntityOrder> makeAddButtonMessage(String name,
                                                             MenuCommands onSelect,
                                                             int drawOrder,
                                                             ButtonData buttonData,
                                                             String actionCode,
                                                             TypeComponentTuple<?>[] extraTuples) {

            return ECSUtil.makeAddEntityMessage(new AddEntityOrder(
                    ArrayUtil.addArrays(
                            new TypeComponentTuple[]{
                                    new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(buttonData.getUnselPos())),
                                    new TypeComponentTuple<>(commandSelectComponentType, onSelect),
                                    new TypeComponentTuple<>(buttonComponentType, buttonData),
                                    new TypeComponentTuple<>(buttonActionComponentType, actionCode),
                                    new TypeComponentTuple<>(spriteInstructionComponentType, new SpriteInstruction(
                                            buttonData.isLocked() ? buttonData.getLockedImage() : buttonData.getUnselImage())),
                                    new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.FOREGROUND, drawOrder)),
                                    new TypeComponentTuple<>(visibleMarker),
                            },
                            extraTuples
                    ), name));
        }

        private Message<AddEntityOrder> makePlayerMessage() {
            return ECSUtil.makeAddEntityMessage(new AddEntityOrder(new TypeComponentTuple[]{
                    new TypeComponentTuple<>(visibleMarker, null),
                    new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(PLAYER_POS)),
                    new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.MIDGROUND, 100)),
                    new TypeComponentTuple<>(spriteInstructionComponentType, new SpriteInstruction("p_idle_1", new ConstCartesianVector(0, 10))),
                    new TypeComponentTuple<>(animationComponentType, new AnimationComponent(
                            new Animation(true, "p_idle_1", "p_idle_2", "p_idle_3", "p_idle_4"),
                            4
                    )),
            }));
        }

        private Message<AddEntityOrder> makeBulletSlowBarrierMessage() {
            return ECSUtil.makeAddEntityMessage(new AddEntityOrder(
                    new TypeComponentTuple[]{//invisible at start
                            new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(PLAYER_POS)),
                            new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.MIDGROUND, -100)),
                            new TypeComponentTuple<>(spriteInstructionComponentType, new SpriteInstruction("large_barrier")),
                            new TypeComponentTuple<>(constantSpriteRotationComponentType, -.5)
                    },
                    ShotPreviewSystem.BULLET_SLOW_BARRIER
            ));
        }
    }
}
