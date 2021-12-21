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

import static internalconfig.game.components.Instructions.CLEAR_SPAWN;
import static internalconfig.game.components.ComponentTypes.*;

class ClearSpawnInstructionHandler implements AbstractInstructionHandler<Void, Void> {

    private final AbstractComponentType<SpawnComponent> spawnComponentType;

    ClearSpawnInstructionHandler(AbstractComponentTypeContainer componentTypeContainer){
        spawnComponentType = componentTypeContainer.getTypeInstance(SpawnComponentType.class);
    }

    @Override
    public Instructions<Void, Void> getInstruction() {
        return CLEAR_SPAWN;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Void, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);
        if (dataStorage.containsComponent(handle, spawnComponentType)) {
            dataStorage.getComponent(handle, spawnComponentType).clear();
        }
        return true;
    }
}