package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;

import static internalconfig.game.components.Instructions.REMOVE_COLLIDABLE;
import static internalconfig.game.components.ComponentTypes.*;

class RemoveCollidableInstructionHandler extends AbstractRemoveComponentInstructionHandler {
    RemoveCollidableInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(REMOVE_COLLIDABLE, componentTypeContainer.getTypeInstance(CollidableMarker.class));
    }
}
