package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import internalconfig.game.systems.gamesystems.GameUtil;
import util.math.RandomUtil;
import util.math.geometry.AABB;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.tuple.Tuple2;
import util.tuple.Tuple4;

import java.util.Random;

@SuppressWarnings("unused")
class BoundRadiusGotoDeceleratingInstructionHandler
        extends AbstractGotoDeceleratingInstructionHandler<Tuple4<AABB, Double, Double, Double>, Tuple2<DoublePoint, Double>> {

    BoundRadiusGotoDeceleratingInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer);
    }

    @Override
    public Instructions<Tuple4<AABB, Double, Double, Double>, Tuple2<DoublePoint, Double>> getInstruction() {
        return Instructions.BOUND_RADIUS_GOTO_DECELERATING;
    }

    @Override
    protected DoublePoint getTarget(AbstractECSInterface ecsInterface,
                                    InstructionNode<Tuple4<AABB, Double, Double, Double>, Tuple2<DoublePoint, Double>> node,
                                    InstructionDataMap dataMap,
                                    int entityID,
                                    DoublePoint currentPos) {
        if (dataMap.containsKey(node)) {
            Tuple2<DoublePoint, Double> dataMapTuple = dataMap.get(node);
            if (dataMapTuple.a != null) {
                return dataMap.get(node).a;
            }
        }

        Tuple4<AABB, Double, Double, Double> nodeDataTuple = node.getData();
        AABB bounds = nodeDataTuple.a;
        double minRadius = nodeDataTuple.b;
        double maxRadius = nodeDataTuple.c;
        DoublePoint target = generateTarget(currentPos, bounds, minRadius, maxRadius,
                GameUtil.getRandom(ecsInterface.getGlobalBoard()));
        if (dataMap.containsKey(node)) {
            dataMap.put(node, new Tuple2<>(target, dataMap.get(node).b));
        }
        else{
            dataMap.put(node, new Tuple2<>(target, null));
        }
        return target;
    }

    private DoublePoint generateTarget(DoublePoint currentPos, AABB bounds, double minRadius, double maxRadius, Random random) {
        if(GeometryUtil.AABBCircleCollision(bounds, currentPos, maxRadius)){
            for(int i = 0; i < 100; ++i){
                double radius = RandomUtil.randDoubleInclusive(minRadius, maxRadius, random);
                double angle = RandomUtil.randDoubleInclusive(0, Math.nextDown(360d), random);
                DoublePoint testPos = new PolarVector(radius, angle).add(currentPos);
                if(GeometryUtil.pointAABBCollision(testPos, bounds)){
                    return testPos;
                }
            }
            throw new RuntimeException("too many failures");
        }
        throw new RuntimeException("circle doesn't collide with bounds!");
    }

    @Override
    protected Tuple2<Double, Double> getInitAndCurrentDistance(AbstractECSInterface ecsInterface,
                                                               InstructionNode<Tuple4<AABB, Double, Double, Double>, Tuple2<DoublePoint, Double>> node,
                                                               InstructionDataMap dataMap,
                                                               int entityID,
                                                               DoublePoint currentPos,
                                                               DoublePoint target) {
        double initDistance;
        double currentDistance;

        if(dataMap.containsKey(node)){
            Tuple2<DoublePoint, Double> dataMapTuple = dataMap.get(node);
            if(dataMapTuple.b != null) {
                initDistance = dataMapTuple.b;
                currentDistance = GeometryUtil.distanceFromAToB(currentPos, target);
            }
            else{
                initDistance = GeometryUtil.distanceFromAToB(currentPos, target);
                currentDistance = initDistance;
                dataMap.put(node, new Tuple2<>(target, initDistance));
            }
        }
        else{
            initDistance = GeometryUtil.distanceFromAToB(currentPos, target);
            currentDistance = initDistance;
            dataMap.put(node, new Tuple2<>(target, initDistance));
        }
        return new Tuple2<>(initDistance, currentDistance);
    }

    @Override
    protected double getMaxSpeed(AbstractECSInterface ecsInterface,
                                 InstructionNode<Tuple4<AABB, Double, Double, Double>, Tuple2<DoublePoint, Double>> node,
                                 InstructionDataMap dataMap,
                                 int entityID) {
        return node.getData().d;
    }
}
