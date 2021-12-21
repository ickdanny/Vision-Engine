package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Instructions;
import util.math.geometry.DoublePoint;

class BoundaryYHighInstructionHandler extends AbstractBoundaryInstructionHandler {
    BoundaryYHighInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(Instructions.BOUNDARY_Y_HIGH, componentTypeContainer);
    }

    @Override
    protected boolean hasPassedBoundary(DoublePoint pos, double bound) {
        return pos.getY() > bound;
    }
}
