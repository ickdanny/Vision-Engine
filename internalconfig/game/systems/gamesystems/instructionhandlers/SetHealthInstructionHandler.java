package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.criticalorders.SetComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.HealthComponent;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import util.messaging.AbstractPublishSubscribeBoard;

class SetHealthInstructionHandler implements AbstractInstructionHandler<Integer, Void>{
    private final AbstractComponentType<HealthComponent> healthComponentType;

    SetHealthInstructionHandler(AbstractComponentTypeContainer componentTypeContainer){
        healthComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.HealthComponentType.class);
    }

    @Override
    public Instructions<Integer, Void> getInstruction() {
        return Instructions.SET_HEALTH;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Integer, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {

        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);

        int health = node.getData();

        if(dataStorage.containsComponent(handle, healthComponentType)){
            dataStorage.getComponent(handle, healthComponentType).setHealth(health);
        }
        else {
            sliceBoard.publishMessage(ECSUtil.makeSetComponentMessage(
                    new SetComponentOrder<>(handle, healthComponentType, new HealthComponent(health))
            ));
        }
        return true;
    }
}
