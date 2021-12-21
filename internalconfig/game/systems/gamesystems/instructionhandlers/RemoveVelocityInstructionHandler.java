package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.components.Instructions.REMOVE_VELOCITY;

class RemoveVelocityInstructionHandler extends AbstractRemoveComponentInstructionHandler {
    RemoveVelocityInstructionHandler(AbstractComponentTypeContainer componentTypeContainer){
        super(REMOVE_VELOCITY, componentTypeContainer.getTypeInstance(VelocityComponentType.class));
    }
}