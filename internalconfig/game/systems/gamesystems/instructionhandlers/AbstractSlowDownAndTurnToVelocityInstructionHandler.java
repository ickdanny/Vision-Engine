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

abstract class AbstractSlowDownAndTurnToVelocityInstructionHandler
        implements AbstractInstructionHandler<Tuple2<AbstractVector, Integer>, Tuple2<Double, Double>> {

    private final AbstractComponentType<VelocityComponent> velocityComponentType;

    AbstractSlowDownAndTurnToVelocityInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        velocityComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.VelocityComponentType.class);
    }

    @Override
    public boolean handleInstruction(
            AbstractECSInterface ecsInterface,
            InstructionNode<Tuple2<AbstractVector, Integer>, Tuple2<Double, Double>> node,
            InstructionDataMap dataMap,
            int entityID) {

        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);

        AbstractVector finalVelocity = node.getData().a;

        if (!dataStorage.containsComponent(handle, velocityComponentType)) {
            sliceBoard.publishMessage(ECSUtil.makeSetComponentMessage(
                    new SetComponentOrder<>(handle, velocityComponentType, new VelocityComponent(finalVelocity))
            ));
            dataMap.remove(node);
            return true;
        }

        double finalSpeed = finalVelocity.getMagnitude();
        Angle finalAngle = finalVelocity.getAngle();
        int ticks = node.getData().b;

        VelocityComponent velocityComponent = dataStorage.getComponent(handle, velocityComponentType);

        AbstractVector oldVelocity = velocityComponent.getVelocity();
        double oldSpeed = oldVelocity.getMagnitude();
        Angle oldAngle = oldVelocity.getAngle();

        Tuple2<Double, Double> velocityIncrement = retrieveOrCreateVelocityIncrement(
                dataMap,
                node,
                ticks,
                oldSpeed,
                finalSpeed,
                oldAngle,
                finalAngle
        );
        double speedIncrement = velocityIncrement.a;
        double angleIncrement = velocityIncrement.b;

        double newSpeed = oldSpeed + speedIncrement;

        boolean hasReachedSpeed = false;

        if (newSpeed <= finalSpeed) {
            newSpeed = finalSpeed;
            hasReachedSpeed = true;
        }

        Angle newAngle = oldVelocity.getAngle().add(angleIncrement);

        AbstractVector slowerVelocity = new PolarVector(newSpeed, newAngle);
        velocityComponent.setVelocity(slowerVelocity);

        if (hasReachedSpeed) {
            dataMap.remove(node);
            return true;
        }
        return false;
    }

    protected abstract Tuple2<Double, Double> retrieveOrCreateVelocityIncrement(
            InstructionDataMap dataMap,
            InstructionNode<Tuple2<AbstractVector, Integer>, Tuple2<Double, Double>> node,
            int ticks,
            double oldSpeed,
            double finalSpeed,
            Angle oldAngle,
            Angle finalAngle
    );
}
