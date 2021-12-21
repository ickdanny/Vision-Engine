package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Instructions;
import util.math.geometry.Angle;

class TurnToInstructionHandler extends AbstractTurnToInstructionHandler {
    TurnToInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(Instructions.TURN_TO, componentTypeContainer);
    }

    @Override
    protected double getWholeAngleShift(Angle initAngle, Angle finalAngle) {
        return finalAngle.smallerDifference(initAngle);
    }
}
