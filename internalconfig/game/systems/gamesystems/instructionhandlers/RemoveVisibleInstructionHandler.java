package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;

import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.ComponentTypes.*;

class RemoveVisibleInstructionHandler extends AbstractRemoveComponentInstructionHandler{

    public RemoveVisibleInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(REMOVE_VISIBLE, componentTypeContainer.getTypeInstance(VisibleMarker.class));
    }
}
