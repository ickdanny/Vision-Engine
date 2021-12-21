package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import internalconfig.game.systems.gamesystems.GameUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.math.geometry.TwoFramePosition;

import static internalconfig.game.components.ComponentTypes.*;

@SuppressWarnings("unused")
class SetVelocityToPlayerInstructionHandler extends AbstractSetVelocityInstructionHandler<Double, Void> {

    private final AbstractComponentType<TwoFramePosition> positionComponentType;

    public SetVelocityToPlayerInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer);
        positionComponentType = componentTypeContainer.getTypeInstance(PositionComponentType.class);
    }

    @Override
    public Instructions<Double, Void> getInstruction() {
        return Instructions.SET_VELOCITY_TO_PLAYER;
    }

    @Override
    protected AbstractVector getVelocity(AbstractECSInterface ecsInterface,
                                         InstructionNode<Double, Void> node,
                                         InstructionDataMap dataMap,
                                         int entityID) {
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);
        if(dataStorage.containsComponent(handle, positionComponentType)){
            DoublePoint pos = GameUtil.getPos(dataStorage, handle, positionComponentType);
            double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, ecsInterface.getSliceBoard(), positionComponentType, pos);
            return new PolarVector(node.getData(), angleToPlayer);
        }
        throw new RuntimeException("entity has no position!");
    }
}
