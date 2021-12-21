package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Instructions;
import util.math.geometry.DoublePoint;

class BoundaryYLowInstructionHandler extends AbstractBoundaryInstructionHandler {
    BoundaryYLowInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(Instructions.BOUNDARY_Y_LOW, componentTypeContainer);
    }

    @Override
    protected boolean hasPassedBoundary(DoublePoint pos, double bound) {
        return pos.getY() < bound;
    }
}
