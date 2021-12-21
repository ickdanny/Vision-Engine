package internalconfig.game.systems.gamesystems.deathhandlers;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.criticalorders.RemoveEntityOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DeathCommands;
import internalconfig.game.components.SpawnComponent;
import internalconfig.game.components.spawns.Spawns;

import static internalconfig.game.components.ComponentTypes.*;

class DeathSpawnHandler implements AbstractDeathHandler {

    private final AbstractComponentType<SpawnComponent> spawnComponentType;
    private final AbstractComponentType<Spawns> deathSpawnComponentType;

    DeathSpawnHandler(AbstractComponentTypeContainer componentTypeContainer){
        spawnComponentType = componentTypeContainer.getTypeInstance(SpawnComponentType.class);
        deathSpawnComponentType = componentTypeContainer.getTypeInstance(DeathSpawnComponentType.class);
    }

    @Override
    public DeathCommands getCommand() {
        return DeathCommands.DEATH_SPAWN;
    }

    @Override
    public void handleDeath(AbstractECSInterface ecsInterface, EntityHandle deadEntity) {
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        if(dataStorage.containsAllComponents(deadEntity, spawnComponentType, deathSpawnComponentType)){
            SpawnComponent spawnComponent = dataStorage.getComponent(deadEntity, spawnComponentType);
            Spawns deathSpawn = dataStorage.getComponent(deadEntity, deathSpawnComponentType);
            spawnComponent.addSpawnUnit(deathSpawn);
            ecsInterface.getSliceBoard().publishMessage(ECSUtil.makeRemoveEntityMessage(new RemoveEntityOrder(deadEntity)));
        }
        else{
            throw new RuntimeException("entity " + deadEntity + "has deathSpawn but no spawnComponent or no deathSpawnComponent!");
        }
    }
}