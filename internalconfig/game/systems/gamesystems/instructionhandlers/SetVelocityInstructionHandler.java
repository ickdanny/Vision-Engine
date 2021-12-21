package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import util.math.geometry.AbstractVector;

import static internalconfig.game.components.Instructions.SET_VELOCITY;

class SetVelocityInstructionHandler extends AbstractSetVelocityInstructionHandler<AbstractVector, Void> {

    SetVelocityInstructionHandler(AbstractComponentTypeContainer componentTypeContainer){
        super(componentTypeContainer);
    }

    @Override
    public Instructions<AbstractVector, Void> getInstruction() {
        return SET_VELOCITY;
    }

    @Override
    protected AbstractVector getVelocity(AbstractECSInterface ecsInterface,
                                         InstructionNode<AbstractVector, Void> node,
                                         InstructionDataMap dataMap,
                                         int entityID) {
        return node.getData();
    }
}
