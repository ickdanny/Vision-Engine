package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import internalconfig.game.components.VelocityComponent;
import util.math.geometry.AbstractVector;
import util.math.geometry.PolarVector;
import util.tuple.Tuple2;

import static internalconfig.game.components.Instructions.ACCELERATE_TO_SPEED;
import static internalconfig.game.components.ComponentTypes.*;

class AccelerateToSpeedInstructionHandler implements AbstractInstructionHandler<Tuple2<Double, Double>, Void> {

    private final AbstractComponentType<VelocityComponent> velocityComponentType;

    AccelerateToSpeedInstructionHandler(AbstractComponentTypeContainer componentTypeContainer){
        velocityComponentType = componentTypeContainer.getTypeInstance(VelocityComponentType.class);
    }

    @Override
    public Instructions<Tuple2<Double, Double>, Void> getInstruction() {
        return ACCELERATE_TO_SPEED;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Tuple2<Double, Double>, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);

        if(!dataStorage.containsComponent(handle, velocityComponentType)){
            return true;
        }

        double targetSpeed = node.getData().a;
        double acceleration = node.getData().b;

        VelocityComponent velocityComponent = dataStorage.getComponent(handle, velocityComponentType);
        AbstractVector velocity = velocityComponent.getVelocity();
        double currentSpeed = velocity.getMagnitude();

        if(currentSpeed >= targetSpeed){
            return true;
        }

        double newSpeed = Math.min(targetSpeed, currentSpeed + acceleration);

        AbstractVector newVelocity = new PolarVector(newSpeed, velocity.getAngle());
        velocityComponent.setVelocity(newVelocity);

        return newSpeed >= targetSpeed;
    }
}
