package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Instructions;
import util.math.geometry.DoublePoint;

import static internalconfig.game.GameConfig.HEIGHT;
import static internalconfig.game.GameConfig.WIDTH;

class BoundaryInstructionHandler extends AbstractBoundaryInstructionHandler {
    BoundaryInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(Instructions.BOUNDARY, componentTypeContainer);
    }

    @Override
    protected boolean hasPassedBoundary(DoublePoint pos, double bound) {
        double y = pos.getY();
        double x = pos.getX();
        return y < bound || y > HEIGHT - bound || x < bound || x > WIDTH - bound;
    }
}
