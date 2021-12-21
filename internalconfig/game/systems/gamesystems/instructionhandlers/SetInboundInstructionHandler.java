package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.system.criticalorders.SetComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;

class SetInboundInstructionHandler implements AbstractInstructionHandler<Double, Void> {

    private final AbstractComponentType<Double> inboundComponentType;

    SetInboundInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        inboundComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.InboundComponentType.class);
    }

    @Override
    public Instructions<Double, Void> getInstruction() {
        return Instructions.SET_INBOUND;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Double, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        double inbound = node.getData();
        ecsInterface.getSliceBoard().publishMessage(ECSUtil.makeSetComponentMessage(
                new SetComponentOrder<>(ecsInterface.getSliceData().makeHandle(entityID), inboundComponentType, inbound))
        );
        return true;
    }
}
