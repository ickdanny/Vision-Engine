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
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;
import util.tuple.Tuple3;

abstract class AbstractSpeedUpAndTurnToVelocityInstructionHandler
        implements AbstractInstructionHandler<Tuple3<AbstractVector, Angle, Integer>, Tuple2<Double, Double>> {

    private final AbstractComponentType<VelocityComponent> velocityComponentType;

    AbstractSpeedUpAndTurnToVelocityInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        velocityComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.VelocityComponentType.class);
    }

    @Override
    public boolean handleInstruction(
            AbstractECSInterface ecsInterface,
            InstructionNode<Tuple3<AbstractVector, Angle, Integer>, Tuple2<Double, Double>> node,
            InstructionDataMap dataMap,
            int entityID) {

        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);

        AbstractVector finalVelocity = node.getData().a;
        double finalSpeed = finalVelocity.getMagnitude();
        Angle initAngle = node.getData().b;
        Angle finalAngle = finalVelocity.getAngle();
        int ticks = node.getData().c;

        Tuple2<Double, Double> velocityIncrement = retrieveOrCreateVelocityIncrement(
                dataMap, node, ticks, finalSpeed, initAngle, finalAngle
        );
        double speedIncrement = velocityIncrement.a;
        double angleIncrement = velocityIncrement.b;

        VelocityComponent velocityComponent = null;
        AbstractVector initVelocity;
        double oldSpeed;
        //if we have no velocity comp OR the magnitude is greater than our final OR the magnitude is less than zero
        if (!dataStorage.containsComponent(handle, velocityComponentType)
                || (oldSpeed = (initVelocity = (velocityComponent = dataStorage.getComponent(handle, velocityComponentType)).getVelocity()).getMagnitude()) > finalSpeed
                || oldSpeed < 0) {
            AbstractVector firstVelocity = new PolarVector(speedIncrement, initAngle.add(angleIncrement));
            if (velocityComponent == null) {
                sliceBoard.publishMessage(ECSUtil.makeSetComponentMessage(
                        new SetComponentOrder<>(handle, velocityComponentType, new VelocityComponent(firstVelocity))
                ));
            } else {
                velocityComponent.setVelocity(firstVelocity);
            }
            if(ticks <= 1){
                dataMap.remove(node);
                return true;
            }
        } else {
            double newSpeed = oldSpeed + speedIncrement;

            boolean hasReachedSpeed = false;

            if (newSpeed >= finalSpeed) {
                newSpeed = finalSpeed;
                hasReachedSpeed = true;
            }

            Angle newAngle = initVelocity.getAngle().add(angleIncrement);

            AbstractVector fasterVelocity = new PolarVector(newSpeed, newAngle);
            velocityComponent.setVelocity(fasterVelocity);

            if(hasReachedSpeed){
                dataMap.remove(node);
                return true;
            }
        }
        return false;
    }

    protected abstract Tuple2<Double, Double> retrieveOrCreateVelocityIncrement(
            InstructionDataMap dataMap,
            InstructionNode<Tuple3<AbstractVector, Angle, Integer>, Tuple2<Double, Double>> node,
            int ticks,
            double finalSpeed,
            Angle initAngle,
            Angle finalAngle
    );
}
