package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DrawOrder;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;

import static internalconfig.game.components.Instructions.SET_DRAW_ORDER;
import static internalconfig.game.components.ComponentTypes.*;

class SetDrawOrderInstructionHandler implements AbstractInstructionHandler<Integer, Void> {

    private final AbstractComponentType<DrawOrder> drawOrderComponentType;

    SetDrawOrderInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        drawOrderComponentType = componentTypeContainer.getTypeInstance(DrawOrderComponentType.class);
    }

    @Override
    public Instructions<Integer, Void> getInstruction() {
        return SET_DRAW_ORDER;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Integer, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {

        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);
        int order = node.getData();

        if (dataStorage.containsComponent(handle, drawOrderComponentType)) {
            DrawOrder drawOrder = dataStorage.getComponent(handle, drawOrderComponentType);
            drawOrder.setOrder(order);
        }
        return true;
    }
}