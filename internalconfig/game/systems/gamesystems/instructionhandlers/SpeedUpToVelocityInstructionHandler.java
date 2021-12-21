package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.criticalorders.SetComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import internalconfig.game.components.VelocityComponent;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.components.Instructions.SPEED_UP_TO_VELOCITY;

class SpeedUpToVelocityInstructionHandler
        implements AbstractInstructionHandler<Tuple2<AbstractVector, Integer>, Double> {

    private final AbstractComponentType<VelocityComponent> velocityComponentType;

    SpeedUpToVelocityInstructionHandler(AbstractComponentTypeContainer componentTypeContainer){
        velocityComponentType = componentTypeContainer.getTypeInstance(VelocityComponentType.class);
    }

    @Override
    public Instructions<Tuple2<AbstractVector, Integer>, Double> getInstruction() {
        return SPEED_UP_TO_VELOCITY;
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
        double finalSpeed = finalVelocity.getMagnitude();
        Angle finalAngle = finalVelocity.getAngle();
        int ticks = node.getData().b;
        double speedIncrement = retrieveOrCreateSpeedIncrement(dataMap, node, ticks, finalSpeed);

        VelocityComponent velocityComponent = null;
        AbstractVector initVelocity;
        double oldSpeed;
        //if we have no velocity comp OR the angle is wrong OR the speed is greater than our final OR the speed is less than zero
        if(!dataStorage.containsComponent(handle, velocityComponentType)
                || (initVelocity = (velocityComponent = dataStorage.getComponent(handle, velocityComponentType)).getVelocity()).getAngle() != finalAngle
                || (oldSpeed = initVelocity.getMagnitude()) > finalSpeed
                || oldSpeed < 0) {
            AbstractVector firstVelocity = new PolarVector(speedIncrement, finalAngle);
            if(velocityComponent == null){
                sliceBoard.publishMessage(ECSUtil.makeSetComponentMessage(
                        new SetComponentOrder<>(handle, velocityComponentType, new VelocityComponent(firstVelocity))
                ));
            }
            else{
                velocityComponent.setVelocity(firstVelocity);
            }
            if(ticks <= 1){
                dataMap.remove(node);
                return true;
            }
        }
        else{
            double newSpeed = oldSpeed + speedIncrement;

            boolean hasReachedSpeed = false;

            if(newSpeed >= finalSpeed){
                newSpeed = finalSpeed;
                hasReachedSpeed = true;
            }

            AbstractVector fasterVelocity = new PolarVector(newSpeed, finalAngle);
            velocityComponent.setVelocity(fasterVelocity);
            if(hasReachedSpeed){
                dataMap.remove(node);
                return true;
            }
        }
        return false;
    }

    private double retrieveOrCreateSpeedIncrement(InstructionDataMap dataMap,
                                                  InstructionNode<Tuple2<AbstractVector, Integer>, Double> node,
                                                  int ticks,
                                                  double finalSpeed){
        if(dataMap.containsKey(node)){
            return dataMap.get(node);
        }
        else{
            double speedIncrement = ticks > 1 ? finalSpeed/ticks : finalSpeed;
            dataMap.put(node, speedIncrement);
            return speedIncrement;
        }
    }
}
