package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.system.criticalorders.SetComponentOrder;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;

@SuppressWarnings("SameParameterValue")
abstract class AbstractSetMarkerInstructionHandler implements AbstractInstructionHandler<Void, Void> {
    private final Instructions<Void, Void> instruction;
    private final AbstractComponentType<Void> marker;

    AbstractSetMarkerInstructionHandler(Instructions<Void, Void> instruction,
                                        AbstractComponentType<Void> marker) {
        this.instruction = instruction;
        this.marker = marker;
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
        ecsInterface.getSliceBoard().publishMessage(ECSUtil.makeSetComponentMessage(
                new SetComponentOrder<>(ecsInterface.getSliceData().makeHandle(entityID), marker, null))
        );
        return true;
    }
}
