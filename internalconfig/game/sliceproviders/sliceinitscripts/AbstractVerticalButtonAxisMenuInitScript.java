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

import static internalconfig.game.components.MenuCommands.*;

@SuppressWarnings("unused")
public abstract class AbstractVerticalButtonAxisMenuInitScript extends AbstractSingleButtonAxisMenuInitScript {

    public AbstractVerticalButtonAxisMenuInitScript(AbstractComponentTypeContainer componentTypeContainer,
                                                    SpriteInstructionSystem spriteInstructionSystem,
                                                    DrawCommandSystem drawCommandSystem) {
        super(componentTypeContainer, spriteInstructionSystem, drawCommandSystem);
    }

    @Override
    protected void hookTwoButtons(AbstractPublishSubscribeBoard sliceBoard,
                                  EntityHandle buttonA, EntityHandle buttonB) {
        sliceBoard.publishMessage(makeAddCommandDownComponentMessage(buttonA));
        sliceBoard.publishMessage(makeAddCommandUpComponentMessage(buttonB));
        sliceBoard.publishMessage(makeAddNeighboringElementDownComponentMessage(buttonA, buttonB));
        sliceBoard.publishMessage(makeAddNeighboringElementUpComponentMessage(buttonA, buttonB));
    }

    protected Message<AddComponentOrder<?>> makeAddCommandDownComponentMessage(EntityHandle handle){
        AddComponentOrder<MenuCommands> order = new AddComponentOrder<>(handle, commandDownComponentType, NAV_DOWN);
        return ECSUtil.makeAddComponentMessage(order);
    }
    protected Message<AddComponentOrder<?>> makeAddCommandUpComponentMessage(EntityHandle handle){
        AddComponentOrder<MenuCommands> order = new AddComponentOrder<>(handle, commandUpComponentType, NAV_UP);
        return ECSUtil.makeAddComponentMessage(order);
    }
    protected Message<AddComponentOrder<?>> makeAddNeighboringElementDownComponentMessage(
            EntityHandle top, EntityHandle bot){
        AddComponentOrder<EntityHandle> order = new AddComponentOrder<>(top, neighboringElementDownComponentType, bot);
        return ECSUtil.makeAddComponentMessage(order);
    }
    protected Message<AddComponentOrder<?>> makeAddNeighboringElementUpComponentMessage(
            EntityHandle top, EntityHandle bot){
        AddComponentOrder<EntityHandle> order = new AddComponentOrder<>(bot, neighboringElementUpComponentType, top);
        return ECSUtil.makeAddComponentMessage(order);
    }
}