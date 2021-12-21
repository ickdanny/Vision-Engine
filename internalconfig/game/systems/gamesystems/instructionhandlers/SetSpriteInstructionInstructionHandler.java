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
import internalconfig.game.components.Instructions;
import internalconfig.game.components.SpriteInstruction;

import static internalconfig.game.components.Instructions.SET_SPRITE_INSTRUCTION;

class SetSpriteInstructionInstructionHandler implements AbstractInstructionHandler<SpriteInstruction, Void> {

    private final AbstractComponentType<SpriteInstruction> spawnComponentType;

    SetSpriteInstructionInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        spawnComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.SpriteInstructionComponentType.class);
    }

    @Override
    public Instructions<SpriteInstruction, Void> getInstruction() {
        return SET_SPRITE_INSTRUCTION;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<SpriteInstruction, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {

        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);
        SpriteInstruction spriteInstruction = node.getData();

        SetComponentOrder<SpriteInstruction> order = new SetComponentOrder<>(handle, spawnComponentType, spriteInstruction);
        ecsInterface.getSliceBoard().publishMessage(ECSUtil.makeSetComponentMessage(order));

        return true;
    }
}
