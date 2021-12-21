package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.tuple.Tuple2;
import util.tuple.Tuple3;

class SpeedUpAndTurnToVelocityLongAngleInstructionHandler
        extends AbstractSpeedUpAndTurnToVelocityInstructionHandler{

    SpeedUpAndTurnToVelocityLongAngleInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer);
    }

    @Override
    public Instructions<Tuple3<AbstractVector, Angle, Integer>, Tuple2<Double, Double>> getInstruction() {
        return Instructions.SPEED_UP_AND_TURN_TO_VELOCITY_LONG_ANGLE;
    }

    @Override
    protected Tuple2<Double, Double> retrieveOrCreateVelocityIncrement(
            InstructionDataMap dataMap,
            InstructionNode<Tuple3<AbstractVector, Angle, Integer>, Tuple2<Double, Double>> node,
            int ticks,
            double finalSpeed,
            Angle initAngle,
            Angle finalAngle) {

        if (dataMap.containsKey(node)) {
            return dataMap.get(node);
        } else {
            double speedIncrement = ticks > 1 ? finalSpeed / ticks : finalSpeed;
            double angleDifference = finalAngle.largerDifference(initAngle);
            double angleIncrement = ticks > 1 ? angleDifference / ticks : angleDifference;
            Tuple2<Double, Double> velocityIncrement = new Tuple2<>(speedIncrement, angleIncrement);
            dataMap.put(node, velocityIncrement);
            return velocityIncrement;
        }
    }
}