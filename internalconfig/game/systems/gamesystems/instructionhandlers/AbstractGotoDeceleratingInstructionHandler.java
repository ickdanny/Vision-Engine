package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.criticalorders.SetComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.VelocityComponent;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.math.geometry.TwoFramePosition;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

@SuppressWarnings("unused")
abstract class AbstractGotoDeceleratingInstructionHandler<T, V> implements AbstractInstructionHandler<T, V> {

    private static final double IGNORE_DISTANCE = .01;

    private static final double EXPONENT_BASE = 2;
    private static final double HORIZONTAL_SHIFT = .1;
    private static final double HORIZONTAL_STRETCH = 7;

    private final AbstractComponentType<TwoFramePosition> positionComponentType;
    private final AbstractComponentType<VelocityComponent> velocityComponentType;

    AbstractGotoDeceleratingInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        positionComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.PositionComponentType.class);
        velocityComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.VelocityComponentType.class);
    }

    @Override
    public final boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<T, V> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {

        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);
        DoublePoint currentPos = dataStorage.getComponent(handle, positionComponentType).getPos();

        DoublePoint target = getTarget(ecsInterface, node, dataMap, entityID, currentPos);

        Tuple2<Double, Double> initAndCurrentDistanceTuple = getInitAndCurrentDistance(
                ecsInterface,
                node,
                dataMap,
                entityID,
                currentPos,
                target);
        double initDistance = initAndCurrentDistanceTuple.a;
        double currentDistance = initAndCurrentDistanceTuple.b;

        if (currentDistance < IGNORE_DISTANCE || initDistance < IGNORE_DISTANCE) {
            dataMap.remove(node);
            if (dataStorage.containsComponent(handle, velocityComponentType)) {
                dataStorage.getComponent(handle, velocityComponentType).setVelocity(new PolarVector(0, 0));
            }
            return true;
        }

        double maxSpeed = getMaxSpeed(ecsInterface, node, dataMap, entityID);

        Angle angle = GeometryUtil.angleFromAToB(currentPos, target);
        double speed = calculateSpeed(initDistance, currentDistance, maxSpeed);

        AbstractVector velocity = new PolarVector(Math.min(speed, currentDistance), angle);

        if (dataStorage.containsComponent(handle, velocityComponentType)) {
            VelocityComponent velocityComponent = dataStorage.getComponent(handle, velocityComponentType);
            velocityComponent.setVelocity(velocity);
        } else {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            VelocityComponent velocityComponent = new VelocityComponent(velocity);
            sliceBoard.publishMessage(ECSUtil.makeSetComponentMessage(
                    new SetComponentOrder<>(handle, velocityComponentType, velocityComponent)
            ));
        }

        return false;
    }

    protected abstract DoublePoint getTarget(AbstractECSInterface ecsInterface,
                                             InstructionNode<T, V> node,
                                             InstructionDataMap dataMap,
                                             int entityID,
                                             DoublePoint currentPos);

    protected abstract Tuple2<Double, Double> getInitAndCurrentDistance(AbstractECSInterface ecsInterface,
                                                                        InstructionNode<T, V> node,
                                                                        InstructionDataMap dataMap,
                                                                        int entityID,
                                                                        DoublePoint currentPos,
                                                                        DoublePoint target);

    protected abstract double getMaxSpeed(AbstractECSInterface ecsInterface,
                                          InstructionNode<T, V> node,
                                          InstructionDataMap dataMap,
                                          int entityID);

    private double calculateSpeed(double initDistance, double currentDistance, double maxSpeed) {
        double distRatio = currentDistance / initDistance;
        return maxSpeed * (1 - (1 / Math.pow(EXPONENT_BASE, (HORIZONTAL_STRETCH * distRatio) + HORIZONTAL_SHIFT)));
    }
}