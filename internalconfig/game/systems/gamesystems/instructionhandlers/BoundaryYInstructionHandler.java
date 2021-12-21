package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Instructions;
import util.math.geometry.DoublePoint;

import static internalconfig.game.GameConfig.HEIGHT;

class BoundaryYInstructionHandler extends AbstractBoundaryInstructionHandler {
    BoundaryYInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(Instructions.BOUNDARY_Y, componentTypeContainer);
    }

    @Override
    protected boolean hasPassedBoundary(DoublePoint pos, double bound) {
        double y = pos.getY();
        return y < bound || y > HEIGHT - bound;
    }
}
