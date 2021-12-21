package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.system.criticalorders.RemoveEntityOrder;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;

import static internalconfig.game.components.Instructions.REMOVE_ENTITY;

class RemoveEntityInstructionHandler implements AbstractInstructionHandler<Void, Void> {
    @Override
    public Instructions<Void, Void> getInstruction() {
        return REMOVE_ENTITY;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Void, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        ecsInterface.getSliceBoard().publishMessage(
                ECSUtil.makeRemoveEntityMessage(new RemoveEntityOrder(ecsInterface.getSliceData().makeHandle(entityID)))
        );
        return true;
    }
}
