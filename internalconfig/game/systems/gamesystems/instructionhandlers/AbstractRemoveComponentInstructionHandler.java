package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.system.criticalorders.RemoveComponentOrder;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;


class AbstractRemoveComponentInstructionHandler implements AbstractInstructionHandler<Void, Void> {

    private final Instructions<Void, Void> instruction;
    private final AbstractComponentType<?> componentType;

    AbstractRemoveComponentInstructionHandler(Instructions<Void, Void> instruction,
                                              AbstractComponentType<?> componentType) {
        this.instruction = instruction;
        this.componentType = componentType;
    }

    @Override
    public Instructions<Void, Void> getInstruction() {
        return instruction;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Void, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        ecsInterface.getSliceBoard().publishMessage(ECSUtil.makeRemoveComponentMessage(
                new RemoveComponentOrder(ecsInterface.getSliceData().makeHandle(entityID), componentType))
        );
        return true;
    }
}
