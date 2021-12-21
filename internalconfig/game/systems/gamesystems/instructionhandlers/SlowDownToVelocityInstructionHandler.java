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
import internalconfig.game.components.Instructions;
import internalconfig.game.components.VelocityComponent;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import static internalconfig.game.components.Instructions.SLOW_DOWN_TO_VELOCITY;

class SlowDownToVelocityInstructionHandler implements AbstractInstructionHandler<Tuple2<AbstractVector, Integer>, Double> {

    private final AbstractComponentType<VelocityComponent> velocityComponentType;

    SlowDownToVelocityInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        velocityComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.VelocityComponentType.class);
    }

    @Override
    public Instructions<Tuple2<AbstractVector, Integer>, Double> getInstruction() {
        return SLOW_DOWN_TO_VELOCITY;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Tuple2<AbstractVector, Integer>, Double> node,
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

        double speedDecrement = retrieveOrCreateSpeedDecrement(
                dataMap,
                node,
                ticks,
                oldSpeed,
                finalSpeed
        );

        double newSpeed = oldSpeed + speedDecrement;

        boolean hasReachedSpeed = false;

        if (newSpeed <= finalSpeed) {
            newSpeed = finalSpeed;
            hasReachedSpeed = true;
        }

        AbstractVector slowerVelocity = new PolarVector(newSpeed, finalAngle);
        velocityComponent.setVelocity(slowerVelocity);

        if (hasReachedSpeed) {
            dataMap.remove(node);
            return true;
        }
        return false;
    }

    private double retrieveOrCreateSpeedDecrement(InstructionDataMap dataMap,
                                                  InstructionNode<Tuple2<AbstractVector, Integer>, Double> node,
                                                  int ticks,
                                                  double initSpeed,
                                                  double finalSpeed) {
        if (dataMap.containsKey(node)) {
            return dataMap.get(node);
        } else {
            double speedDifference = finalSpeed - initSpeed;
            double speedDecrement = ticks > 1 ? speedDifference / ticks : speedDifference;
            dataMap.put(node, speedDecrement);
            return speedDecrement;
        }
    }
}
