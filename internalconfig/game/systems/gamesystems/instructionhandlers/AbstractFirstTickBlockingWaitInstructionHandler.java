package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;

abstract class AbstractFirstTickBlockingWaitInstructionHandler<T> implements AbstractInstructionHandler<T, Object> {

    @Override
    public final boolean handleInstruction(AbstractECSInterface ecsInterface,
                                           InstructionNode<T, Object> node,
                                           InstructionDataMap dataMap,
                                           int entityID) {
        //block the first tick
        if(!dataMap.containsKey(node)){
            dataMap.put(node, new Object());
            return false;
        }

        if(isDoneWaiting(ecsInterface, node, dataMap, entityID)) {
            dataMap.remove(node);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    protected abstract boolean isDoneWaiting(AbstractECSInterface ecsInterface,
                                             InstructionNode<T, Object> node,
                                             InstructionDataMap dataMap,
                                             int entityID);
}
