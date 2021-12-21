package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Instructions;
import util.math.geometry.DoublePoint;

import static internalconfig.game.GameConfig.WIDTH;

class BoundaryXInstructionHandler extends AbstractBoundaryInstructionHandler {
    BoundaryXInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(Instructions.BOUNDARY_X, componentTypeContainer);
    }

    @Override
    protected boolean hasPassedBoundary(DoublePoint pos, double bound) {
        double x = pos.getX();
        return x < bound || x > WIDTH - bound;
    }
}
