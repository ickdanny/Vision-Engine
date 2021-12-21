package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import util.math.geometry.DoublePoint;
import util.math.geometry.TwoFramePosition;

import static internalconfig.game.components.ComponentTypes.*;

abstract class AbstractBoundaryInstructionHandler implements AbstractInstructionHandler<Double, Void> {

    private final Instructions<Double, Void> instruction;
    protected final AbstractComponentType<TwoFramePosition> positionComponentType;

    AbstractBoundaryInstructionHandler(Instructions<Double, Void> instruction,
                                       AbstractComponentTypeContainer componentTypeContainer) {
        this.instruction = instruction;
        positionComponentType = componentTypeContainer.getTypeInstance(PositionComponentType.class);
    }

    @Override
    public Instructions<Double, Void> getInstruction() {
        return instruction;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Double, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);
        TwoFramePosition twoFramePosition = dataStorage.getComponent(handle, positionComponentType);
        return hasPassedBoundary(twoFramePosition.getPos(), node.getData());
    }

    protected abstract boolean hasPassedBoundary(DoublePoint pos, double bound);
}
