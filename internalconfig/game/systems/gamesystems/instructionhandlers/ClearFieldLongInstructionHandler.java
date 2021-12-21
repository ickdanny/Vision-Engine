package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import internalconfig.game.components.SpawnComponent;
import internalconfig.game.components.spawns.OtherGameSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;

class ClearFieldLongInstructionHandler implements AbstractInstructionHandler<Void, Void> {

    private final AbstractComponentType<SpawnComponent> spawnComponentType;

    ClearFieldLongInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        spawnComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.SpawnComponentType.class);
    }

    @Override
    public Instructions<Void, Void> getInstruction() {
        return Instructions.CLEAR_FIELD_LONG;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Void, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {

        EntityHandle spawner = GameUtil.getSpawner(ecsInterface.getSliceBoard());
        SpawnComponent spawnComponent = ecsInterface.getSliceData().getComponent(spawner, spawnComponentType);
        spawnComponent.addSpawnUnit(OtherGameSpawns.LONG_BULLET_CLEAR);
        return true;
    }
}