package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;

interface AbstractInstructionHandler<T, V> {
    Instructions<T, V> getInstruction();
    boolean handleInstruction(AbstractECSInterface ecsInterface,
                              InstructionNode<T, V> node,
                              InstructionDataMap dataMap,
                              int entityID);
}
