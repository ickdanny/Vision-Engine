package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.tuple.Tuple2;

import static internalconfig.game.components.Instructions.SLOW_DOWN_AND_TURN_TO_VELOCITY;

class SlowDownAndTurnToVelocityInstructionHandler extends AbstractSlowDownAndTurnToVelocityInstructionHandler {
    SlowDownAndTurnToVelocityInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer);
    }

    @Override
    protected Tuple2<Double, Double> retrieveOrCreateVelocityIncrement(InstructionDataMap dataMap,
                                                                       InstructionNode<Tuple2<AbstractVector, Integer>, Tuple2<Double, Double>> node,
                                                                       int ticks,
                                                                       double oldSpeed,
                                                                       double finalSpeed,
                                                                       Angle oldAngle,
                                                                       Angle finalAngle) {
        if (dataMap.containsKey(node)) {
            return dataMap.get(node);
        } else {
            double speedDifference = finalSpeed - oldSpeed;
            double speedIncrement = ticks > 1
                    ? speedDifference / ticks
                    : speedDifference;
            double angleDifference = finalAngle.smallerDifference(oldAngle);
            double angleIncrement = ticks > 1 ? angleDifference / ticks : angleDifference;
            Tuple2<Double, Double> velocityIncrement = new Tuple2<>(speedIncrement, angleIncrement);
            dataMap.put(node, velocityIncrement);
            return velocityIncrement;
        }
    }

    @Override
    public Instructions<Tuple2<AbstractVector, Integer>, Tuple2<Double, Double>> getInstruction() {
        return SLOW_DOWN_AND_TURN_TO_VELOCITY;
    }
}
