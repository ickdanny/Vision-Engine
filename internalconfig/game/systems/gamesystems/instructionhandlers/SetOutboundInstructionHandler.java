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

class SetOutboundInstructionHandler implements AbstractInstructionHandler<Double, Void> {

    private final AbstractComponentType<Double> outboundComponentType;

    SetOutboundInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        outboundComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.OutboundComponentType.class);
    }

    @Override
    public Instructions<Double, Void> getInstruction() {
        return Instructions.SET_OUTBOUND;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Double, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        double outbound = node.getData();
        ecsInterface.getSliceBoard().publishMessage(ECSUtil.makeSetComponentMessage(
                new SetComponentOrder<>(ecsInterface.getSliceData().makeHandle(entityID), outboundComponentType, outbound))
        );
        return true;
    }
}
