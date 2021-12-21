package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Instructions;
import util.math.geometry.Angle;

class TurnToLongAngleInstructionHandler extends AbstractTurnToInstructionHandler {
    TurnToLongAngleInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(Instructions.TURN_TO_LONG_ANGLE, componentTypeContainer);
    }

    @Override
    protected double getWholeAngleShift(Angle initAngle, Angle finalAngle) {
        return finalAngle.largerDifference(initAngle);
    }
}