package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Instructions;

import static internalconfig.game.components.ComponentTypes.*;

class RemoveHealthInstructionHandler extends AbstractRemoveComponentInstructionHandler{
    public RemoveHealthInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(Instructions.REMOVE_HEALTH, componentTypeContainer.getTypeInstance(HealthComponentType.class));
    }
}