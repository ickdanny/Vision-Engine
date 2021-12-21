package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import internalconfig.game.systems.gamesystems.GameUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.PolarVector;
import util.tuple.Tuple4;

import java.util.Random;

class SetRandomVelocityInstructionHandler
        extends AbstractSetVelocityInstructionHandler<Tuple4<Double, Double, Double, Double>, Void> {

    SetRandomVelocityInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer);
    }

    @Override
    public Instructions<Tuple4<Double, Double, Double, Double>, Void> getInstruction() {
        return Instructions.SET_RANDOM_VELOCITY;
    }

    @Override
    protected AbstractVector getVelocity(AbstractECSInterface ecsInterface,
                                         InstructionNode<Tuple4<Double, Double, Double, Double>, Void> node,
                                         InstructionDataMap dataMap,
                                         int entityID) {
        Tuple4<Double, Double, Double, Double> tuple = node.getData();
        Random random = GameUtil.getRandom(ecsInterface.getGlobalBoard());
        double speed = RandomUtil.randDoubleInclusive(tuple.a, tuple.b, random);
        double angle = RandomUtil.randDoubleInclusive(tuple.c, tuple.d, random);
        return new PolarVector(speed, angle);
    }
}
