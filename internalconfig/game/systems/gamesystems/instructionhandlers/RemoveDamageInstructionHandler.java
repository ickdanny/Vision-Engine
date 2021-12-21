package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Instructions;

import static internalconfig.game.components.ComponentTypes.*;

class RemoveDamageInstructionHandler extends AbstractRemoveComponentInstructionHandler {
    RemoveDamageInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(Instructions.REMOVE_DAMAGE, componentTypeContainer.getTypeInstance(DamageComponentType.class));
    }
}
