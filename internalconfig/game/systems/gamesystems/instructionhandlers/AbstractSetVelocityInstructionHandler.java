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

abstract class AbstractSetVelocityInstructionHandler<T, V> implements AbstractInstructionHandler<T, V> {

    protected final AbstractComponentType<VelocityComponent> velocityComponentType;

    AbstractSetVelocityInstructionHandler(AbstractComponentTypeContainer componentTypeContainer){
        velocityComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.VelocityComponentType.class);
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<T, V> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {

        AbstractVector velocity = getVelocity(ecsInterface, node, dataMap, entityID);

        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);
        if (dataStorage.containsComponent(handle, velocityComponentType)) {
            VelocityComponent velocityComponent = dataStorage.getComponent(handle, velocityComponentType);
            velocityComponent.setVelocity(velocity);
        }
        else {
            ecsInterface.getSliceBoard().publishMessage(ECSUtil.makeSetComponentMessage(
                    new SetComponentOrder<>(ecsInterface.getSliceData().makeHandle(entityID),
                            velocityComponentType,
                            new VelocityComponent(velocity)))
            );
        }
        return true;
    }

    @SuppressWarnings("unused")
    protected abstract AbstractVector getVelocity(AbstractECSInterface ecsInterface,
                                                  InstructionNode<T, V> node,
                                                  InstructionDataMap dataMap,
                                                  int entityID);
}