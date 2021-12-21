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
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.components.Instructions.SLOW_TO_HALT;

class SlowToHaltInstructionHandler implements AbstractInstructionHandler<Integer, Double> {
    private final AbstractComponentType<VelocityComponent> velocityComponentType;

    SlowToHaltInstructionHandler(AbstractComponentTypeContainer componentTypeContainer){
        velocityComponentType = componentTypeContainer.getTypeInstance(VelocityComponentType.class);
    }

    @Override
    public Instructions<Integer, Double> getInstruction() {
        return SLOW_TO_HALT;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Integer, Double> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {

        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);

        if(dataStorage.containsComponent(handle, velocityComponentType)){
            VelocityComponent velocityComponent = dataStorage.getComponent(handle, velocityComponentType);
            AbstractVector initVelocity = velocityComponent.getVelocity();
            double oldMagnitude = initVelocity.getMagnitude();
            double slow = retrieveOrCreateSlow(dataMap, node, oldMagnitude);
            double newMagnitude = oldMagnitude - slow;

            if(newMagnitude <= 0){
//                sliceBoard.publishMessage(ECSUtil.makeRemoveComponentMessage(
//                        new RemoveComponentOrder(handle, velocityComponentType)
//                ));
                AbstractVector slowedVelocity = new PolarVector(0, initVelocity.getAngle());
                velocityComponent.setVelocity(slowedVelocity);
                dataMap.remove(node);
                return true;
            }
            AbstractVector slowedVelocity = new PolarVector(newMagnitude, initVelocity.getAngle());
            velocityComponent.setVelocity(slowedVelocity);
            return false;
        }
        dataMap.remove(node);
        return true;
    }

    private double retrieveOrCreateSlow(InstructionDataMap dataMap,
                                        InstructionNode<Integer, Double> node,
                                        double oldMagnitude){
        if(dataMap.containsKey(node)){
            return dataMap.get(node);
        }
        else{
            int ticks = node.getData();
            double slow = Math.abs(ticks > 0 ? oldMagnitude/node.getData() : oldMagnitude);
            dataMap.put(node, slow);
            return slow;
        }
    }


}
