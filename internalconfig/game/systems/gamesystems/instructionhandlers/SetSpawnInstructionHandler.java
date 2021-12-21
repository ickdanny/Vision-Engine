package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.criticalorders.AddComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import internalconfig.game.components.SpawnComponent;
import internalconfig.game.components.spawns.Spawns;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.components.Instructions.SET_SPAWN;

class SetSpawnInstructionHandler implements AbstractInstructionHandler<Spawns, Void> {

    private final AbstractComponentType<SpawnComponent> spawnComponentType;

    SetSpawnInstructionHandler(AbstractComponentTypeContainer componentTypeContainer){
        spawnComponentType = componentTypeContainer.getTypeInstance(SpawnComponentType.class);
    }

    @Override
    public Instructions<Spawns, Void> getInstruction() {
        return SET_SPAWN;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Spawns, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {

        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);
        Spawns spawn = node.getData();

        if(dataStorage.containsComponent(handle, spawnComponentType)){
            SpawnComponent spawnComponent = dataStorage.getComponent(handle, spawnComponentType);
            spawnComponent.clear();
            spawnComponent.addSpawnUnit(spawn);
        }
        else{
            SpawnComponent spawnComponent = new SpawnComponent().addSpawnUnit(spawn);
            AddComponentOrder<SpawnComponent> order = new AddComponentOrder<>(
                    handle, spawnComponentType, spawnComponent
            );
            ecsInterface.getSliceBoard().publishMessage(ECSUtil.makeAddComponentMessage(order));
        }

        return true;
    }
}
