package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;

import static internalconfig.game.components.Instructions.SET_COLLIDABLE;
import static internalconfig.game.components.ComponentTypes.CollidableMarker;

class SetCollidableInstructionHandler extends AbstractSetMarkerInstructionHandler {
    SetCollidableInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(SET_COLLIDABLE, componentTypeContainer.getTypeInstance(CollidableMarker.class));
    }
}
