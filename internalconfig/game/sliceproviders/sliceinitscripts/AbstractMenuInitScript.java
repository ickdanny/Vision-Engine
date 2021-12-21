package internalconfig.game.sliceproviders.sliceinitscripts;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.component.TypeComponentTuple;
import ecs.datastorage.AbstractDataStorage;
import ecs.datastorage.AbstractSliceInitScript;
import ecs.entity.EntityHandle;
import ecs.system.criticalorders.AddEntityOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ButtonData;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.DrawOrder;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.LockConditions;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.systems.graphicssystems.SpriteInstructionSystem;
import internalconfig.game.systems.graphicssystems.DrawCommandSystem;
import util.math.geometry.DoublePoint;
import util.math.geometry.TwoFramePosition;
import internalconfig.game.components.MenuCommands;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.game.components.ComponentTypes.*;

@SuppressWarnings("unused")
public abstract class AbstractMenuInitScript extends AbstractSliceInitScript {

    protected final AbstractComponentTypeContainer componentTypeContainer;

    protected final SpriteInstructionSystem spriteInstructionSystem;
    protected final DrawCommandSystem drawCommandSystem;

    protected final AbstractComponentType<MenuCommands> commandUpComponentType;
    protected final AbstractComponentType<MenuCommands> commandDownComponentType;
    protected final AbstractComponentType<MenuCommands> commandLeftComponentType;
    protected final AbstractComponentType<MenuCommands> commandRightComponentType;

    protected final AbstractComponentType<EntityHandle> neighboringElementUpComponentType;
    protected final AbstractComponentType<EntityHandle> neighboringElementDownComponentType;
    protected final AbstractComponentType<EntityHandle> neighboringElementLeftComponentType;
    protected final AbstractComponentType<EntityHandle> neighboringElementRightComponentType;

    protected final AbstractComponentType<MenuCommands> commandSelectComponentType;

    protected final AbstractComponentType<Void> defaultSelectedElementMarker;

    protected final AbstractComponentType<TwoFramePosition> positionComponentType;
    protected final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
    protected final AbstractComponentType<DrawOrder> drawOrderComponentType;
    protected final AbstractComponentType<Void> visibleMarker;
    protected final AbstractComponentType<ButtonData> buttonComponentType;
    protected final AbstractComponentType<String> buttonActionComponentType;
    protected final AbstractComponentType<LockConditions> lockConditionComponentType;

    public AbstractMenuInitScript(AbstractComponentTypeContainer componentTypeContainer,
                                  SpriteInstructionSystem spriteInstructionSystem,
                                  DrawCommandSystem drawCommandSystem) {
        this.componentTypeContainer = componentTypeContainer;

        this.spriteInstructionSystem = spriteInstructionSystem;
        this.drawCommandSystem = drawCommandSystem;

        commandUpComponentType =
                componentTypeContainer.getTypeInstance(ComponentTypes.MenuCommandComponentType_Up.class);
        commandDownComponentType =
                componentTypeContainer.getTypeInstance(ComponentTypes.MenuCommandComponentType_Down.class);
        commandLeftComponentType =
                componentTypeContainer.getTypeInstance(ComponentTypes.MenuCommandComponentType_Left.class);
        commandRightComponentType =
                componentTypeContainer.getTypeInstance(ComponentTypes.MenuCommandComponentType_Right.class);

        neighboringElementUpComponentType =
                componentTypeContainer.getTypeInstance(ComponentTypes.NeighboringElementComponentType_Up.class);
        neighboringElementDownComponentType =
                componentTypeContainer.getTypeInstance(ComponentTypes.NeighboringElementComponentType_Down.class);
        neighboringElementLeftComponentType =
                componentTypeContainer.getTypeInstance(ComponentTypes.NeighboringElementComponentType_Left.class);
        neighboringElementRightComponentType =
                componentTypeContainer.getTypeInstance(ComponentTypes.NeighboringElementComponentType_Right.class);

        commandSelectComponentType =
                componentTypeContainer.getTypeInstance(ComponentTypes.MenuCommandComponentType_Select.class);
        defaultSelectedElementMarker =
                componentTypeContainer.getTypeInstance(ComponentTypes.DefaultSelectedElementMarker.class);

        positionComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.PositionComponentType.class);
        spriteInstructionComponentType =
                componentTypeContainer.getTypeInstance(ComponentTypes.SpriteInstructionComponentType.class);
        drawOrderComponentType =
                componentTypeContainer.getTypeInstance(DrawOrderComponentType.class);
        visibleMarker = componentTypeContainer.getTypeInstance(VisibleMarker.class);
        buttonComponentType = componentTypeContainer.getTypeInstance(ButtonComponentType.class);
        buttonActionComponentType = componentTypeContainer.getTypeInstance(ButtonActionType.class);
        lockConditionComponentType = componentTypeContainer.getTypeInstance(LockConditionComponentType.class);
    }

    @Override
    public void runOn(AbstractECSInterface ecsInterface) {
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        publishOrders(sliceBoard);
        carryOutCriticalOrders(dataStorage);
        hookUpElements(sliceBoard);
        carryOutCriticalOrders(dataStorage);
        spriteInstructionSystem.makeInstance().run(ecsInterface, 0d);
        carryOutCriticalOrders(dataStorage);
        drawCommandSystem.makeInstance().run(ecsInterface, 0d);
    }

    protected abstract void publishOrders(AbstractPublishSubscribeBoard sliceBoard);

    protected abstract void hookUpElements(AbstractPublishSubscribeBoard sliceBoard);

    protected Message<AddEntityOrder> makeBackgroundMessage(String image, int drawOrder) {

        AddEntityOrder order = new AddEntityOrder(new TypeComponentTuple[]{
                new TypeComponentTuple<>(positionComponentType, new TwoFramePosition()),
                new TypeComponentTuple<>(spriteInstructionComponentType, new SpriteInstruction(image)),
                new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.BACKGROUND, drawOrder)),
                new TypeComponentTuple<>(visibleMarker),
        });
        return ECSUtil.makeAddEntityMessage(order);
    }

    protected Message<AddEntityOrder> makeForegroundMessage(String image, DoublePoint pos, int drawOrder) {

        AddEntityOrder order = new AddEntityOrder(new TypeComponentTuple[]{
                new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(pos)),
                new TypeComponentTuple<>(spriteInstructionComponentType, new SpriteInstruction(image)),
                new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.FOREGROUND, drawOrder)),
                new TypeComponentTuple<>(visibleMarker),
        });
        return ECSUtil.makeAddEntityMessage(order);
    }

    protected Message<AddEntityOrder> makeForegroundMessage(String image, int drawOrder) {

        AddEntityOrder order = new AddEntityOrder(new TypeComponentTuple[]{
                new TypeComponentTuple<>(positionComponentType, new TwoFramePosition()),
                new TypeComponentTuple<>(spriteInstructionComponentType, new SpriteInstruction(image)),
                new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.FOREGROUND, drawOrder)),
                new TypeComponentTuple<>(visibleMarker),
        });
        return ECSUtil.makeAddEntityMessage(order);
    }

    protected Message<AddEntityOrder> makeBackgroundMessage(String image, DoublePoint pos, int drawOrder) {

        AddEntityOrder order = new AddEntityOrder(new TypeComponentTuple[]{
                new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(pos)),
                new TypeComponentTuple<>(spriteInstructionComponentType, new SpriteInstruction(image)),
                new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.BACKGROUND, drawOrder)),
                new TypeComponentTuple<>(visibleMarker),
        });
        return ECSUtil.makeAddEntityMessage(order);
    }

    protected Message<AddEntityOrder> makeAddFirstButtonMessage(String name,
                                                                MenuCommands onSelect,
                                                                int drawOrder,
                                                                ButtonData buttonData) {

        return ECSUtil.makeAddEntityMessage(new AddEntityOrder(new TypeComponentTuple[]{
                new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(buttonData.getSelPos())),
                new TypeComponentTuple<>(commandSelectComponentType, onSelect),
                new TypeComponentTuple<>(buttonComponentType, buttonData),
                new TypeComponentTuple<>(spriteInstructionComponentType,
                        new SpriteInstruction(buttonData.getSelImage())),
                new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.MIDGROUND, drawOrder)),
                new TypeComponentTuple<>(defaultSelectedElementMarker),
                new TypeComponentTuple<>(visibleMarker),
        }, name));
    }

    protected Message<AddEntityOrder> makeAddFirstButtonMessage(String name,
                                                                MenuCommands onSelect,
                                                                int drawOrder,
                                                                ButtonData buttonData,
                                                                String actionCode) {

        return ECSUtil.makeAddEntityMessage(new AddEntityOrder(new TypeComponentTuple[]{
                new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(buttonData.getSelPos())),
                new TypeComponentTuple<>(commandSelectComponentType, onSelect),
                new TypeComponentTuple<>(buttonComponentType, buttonData),
                new TypeComponentTuple<>(buttonActionComponentType, actionCode),
                new TypeComponentTuple<>(spriteInstructionComponentType,
                        new SpriteInstruction(buttonData.getSelImage())),
                new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.MIDGROUND, drawOrder)),
                new TypeComponentTuple<>(defaultSelectedElementMarker),
                new TypeComponentTuple<>(visibleMarker),
        }, name));
    }

    protected Message<AddEntityOrder> makeAddButtonMessage(String name,
                                                           MenuCommands onSelect,
                                                           int drawOrder,
                                                           ButtonData buttonData) {

        return ECSUtil.makeAddEntityMessage(new AddEntityOrder(new TypeComponentTuple[]{
                new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(buttonData.getUnselPos())),
                new TypeComponentTuple<>(commandSelectComponentType, onSelect),
                new TypeComponentTuple<>(buttonComponentType, buttonData),
                new TypeComponentTuple<>(spriteInstructionComponentType, new SpriteInstruction(
                        buttonData.isLocked() ? buttonData.getLockedImage() : buttonData.getUnselImage())),
                new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.MIDGROUND, drawOrder)),
                new TypeComponentTuple<>(visibleMarker),
        }, name));
    }

    protected Message<AddEntityOrder> makeAddButtonMessage(String name,
                                                           MenuCommands onSelect,
                                                           int drawOrder,
                                                           ButtonData buttonData,
                                                           String actionCode) {

        return ECSUtil.makeAddEntityMessage(new AddEntityOrder(new TypeComponentTuple[]{
                new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(buttonData.getUnselPos())),
                new TypeComponentTuple<>(commandSelectComponentType, onSelect),
                new TypeComponentTuple<>(buttonComponentType, buttonData),
                new TypeComponentTuple<>(buttonActionComponentType, actionCode),
                new TypeComponentTuple<>(spriteInstructionComponentType, new SpriteInstruction(
                        buttonData.isLocked() ? buttonData.getLockedImage() : buttonData.getUnselImage())),
                new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.MIDGROUND, drawOrder)),
                new TypeComponentTuple<>(visibleMarker),
        }, name));
    }

    protected Message<AddEntityOrder> makeAddLockedButtonMessage(String name,
                                                                 MenuCommands onSelect,
                                                                 int drawOrder,
                                                                 ButtonData buttonData,
                                                                 LockConditions lockCondition,
                                                                 String actionCode) {
        return ECSUtil.makeAddEntityMessage(new AddEntityOrder(new TypeComponentTuple[]{
                new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(buttonData.getUnselPos())),
                new TypeComponentTuple<>(commandSelectComponentType, onSelect),
                new TypeComponentTuple<>(buttonComponentType, buttonData),
                new TypeComponentTuple<>(lockConditionComponentType, lockCondition),
                new TypeComponentTuple<>(buttonActionComponentType, actionCode),
                new TypeComponentTuple<>(spriteInstructionComponentType, new SpriteInstruction(
                        buttonData.isLocked() ? buttonData.getLockedImage() : buttonData.getUnselImage())),
                new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.MIDGROUND, drawOrder)),
                new TypeComponentTuple<>(visibleMarker),
        }, name));
    }
}