package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import internalconfig.game.components.SpawnComponent;

import static internalconfig.game.components.Instructions.WAIT_UNTIL_SPAWN_COMPONENT_EMPTY;
import static internalconfig.game.components.ComponentTypes.*;

class WaitUntilSpawnComponentEmptyInstructionHandler extends AbstractFirstTickBlockingWaitInstructionHandler<Void> {

    private final AbstractComponentType<SpawnComponent> spawnComponentType;

    WaitUntilSpawnComponentEmptyInstructionHandler(AbstractComponentTypeContainer componentTypeContainer){
        spawnComponentType = componentTypeContainer.getTypeInstance(SpawnComponentType.class);
    }

    @Override
    public Instructions<Void, Object> getInstruction() {
        return WAIT_UNTIL_SPAWN_COMPONENT_EMPTY;
    }

    @Override
    public boolean isDoneWaiting(AbstractECSInterface ecsInterface,
                                     InstructionNode<Void, Object> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);

        if(dataStorage.containsComponent(handle, spawnComponentType)){
            SpawnComponent spawnComponent = dataStorage.getComponent(handle, spawnComponentType);
            return spawnComponent.isEmpty();
        }
        return true;
    }
}
