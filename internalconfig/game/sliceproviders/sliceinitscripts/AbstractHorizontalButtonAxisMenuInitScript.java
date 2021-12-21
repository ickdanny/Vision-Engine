package internalconfig.game.sliceproviders.sliceinitscripts;

import ecs.ECSUtil;
import ecs.entity.EntityHandle;
import ecs.system.criticalorders.AddComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.graphicssystems.SpriteInstructionSystem;
import internalconfig.game.systems.graphicssystems.DrawCommandSystem;
import internalconfig.game.components.MenuCommands;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.game.components.MenuCommands.NAV_LEFT;
import static internalconfig.game.components.MenuCommands.NAV_RIGHT;

@SuppressWarnings("unused")
public abstract class AbstractHorizontalButtonAxisMenuInitScript extends AbstractSingleButtonAxisMenuInitScript {
    public AbstractHorizontalButtonAxisMenuInitScript(AbstractComponentTypeContainer componentTypeContainer,
                                                      SpriteInstructionSystem spriteInstructionSystem,
                                                      DrawCommandSystem drawCommandSystem) {
        super(componentTypeContainer, spriteInstructionSystem, drawCommandSystem);
    }

    @Override
    protected void hookTwoButtons(AbstractPublishSubscribeBoard sliceBoard,
                                  EntityHandle buttonA, EntityHandle buttonB) {
        sliceBoard.publishMessage(makeAddCommandRightComponentMessage(buttonA));
        sliceBoard.publishMessage(makeAddCommandLeftComponentMessage(buttonB));
        sliceBoard.publishMessage(makeAddNeighboringElementRightComponentMessage(buttonA, buttonB));
        sliceBoard.publishMessage(makeAddNeighboringElementLeftComponentMessage(buttonA, buttonB));
    }

    private Message<AddComponentOrder<?>> makeAddCommandRightComponentMessage(EntityHandle handle){
        AddComponentOrder<MenuCommands> order = new AddComponentOrder<>(handle, commandRightComponentType, NAV_RIGHT);
        return ECSUtil.makeAddComponentMessage(order);
    }
    private Message<AddComponentOrder<?>> makeAddCommandLeftComponentMessage(EntityHandle handle){
        AddComponentOrder<MenuCommands> order = new AddComponentOrder<>(handle, commandLeftComponentType, NAV_LEFT);
        return ECSUtil.makeAddComponentMessage(order);
    }
    private Message<AddComponentOrder<?>> makeAddNeighboringElementRightComponentMessage(
            EntityHandle left, EntityHandle right){
        AddComponentOrder<EntityHandle> order = new AddComponentOrder<>(left, neighboringElementRightComponentType, right);
        return ECSUtil.makeAddComponentMessage(order);
    }
    private Message<AddComponentOrder<?>> makeAddNeighboringElementLeftComponentMessage(
            EntityHandle left, EntityHandle right){
        AddComponentOrder<EntityHandle> order = new AddComponentOrder<>(right, neighboringElementLeftComponentType, left);
        return ECSUtil.makeAddComponentMessage(order);
    }
}
