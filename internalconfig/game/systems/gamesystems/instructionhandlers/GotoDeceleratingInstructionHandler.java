package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.tuple.Tuple2;

class GotoDeceleratingInstructionHandler
        extends AbstractGotoDeceleratingInstructionHandler<Tuple2<DoublePoint, Double>, Double> {

    public GotoDeceleratingInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer);
    }

    @Override
    public Instructions<Tuple2<DoublePoint, Double>, Double> getInstruction() {
        return Instructions.GOTO_DECELERATING;
    }

    @Override
    protected DoublePoint getTarget(AbstractECSInterface ecsInterface,
                                    InstructionNode<Tuple2<DoublePoint, Double>, Double> node,
                                    InstructionDataMap dataMap,
                                    int entityID,
                                    DoublePoint currentPos) {
        return node.getData().a;
    }

    @Override
    protected Tuple2<Double, Double> getInitAndCurrentDistance(AbstractECSInterface ecsInterface,
                                                               InstructionNode<Tuple2<DoublePoint, Double>, Double> node,
                                                               InstructionDataMap dataMap,
                                                               int entityID,
                                                               DoublePoint currentPos,
                                                               DoublePoint target) {
        double initDistance;
        double currentDistance;
        if(dataMap.containsKey(node)){
            initDistance = dataMap.get(node);
            currentDistance = GeometryUtil.distanceFromAToB(currentPos, target);
        }
        else{
            initDistance = GeometryUtil.distanceFromAToB(currentPos, target);
            currentDistance = initDistance;
            dataMap.put(node, initDistance);
        }
        return new Tuple2<>(initDistance, currentDistance);
    }

    @Override
    protected double getMaxSpeed(AbstractECSInterface ecsInterface,
                                 InstructionNode<Tuple2<DoublePoint, Double>, Double> node,
                                 InstructionDataMap dataMap,
                                 int entityID) {
        return node.getData().b;
    }
}
