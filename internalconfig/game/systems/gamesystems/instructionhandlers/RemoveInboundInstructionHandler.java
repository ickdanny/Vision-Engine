package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Instructions;

import static internalconfig.game.components.ComponentTypes.*;

class RemoveInboundInstructionHandler extends AbstractRemoveComponentInstructionHandler {
    RemoveInboundInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(Instructions.REMOVE_INBOUND, componentTypeContainer.getTypeInstance(InboundComponentType.class));
    }
}
