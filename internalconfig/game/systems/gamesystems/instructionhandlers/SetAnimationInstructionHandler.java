package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.criticalorders.SetComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.AnimationComponent;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;

import static internalconfig.game.components.Instructions.SET_ANIMATION_INSTRUCTION;

class SetAnimationInstructionHandler implements AbstractInstructionHandler<AnimationComponent, Void> {

    private final AbstractComponentType<AnimationComponent> animationComponentType;

    SetAnimationInstructionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        animationComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.AnimationComponentType.class);
    }

    @Override
    public Instructions<AnimationComponent, Void> getInstruction() {
        return SET_ANIMATION_INSTRUCTION;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<AnimationComponent, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {

        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);
        AnimationComponent animationComponent = node.getData();

        SetComponentOrder<AnimationComponent> order = new SetComponentOrder<>(handle, animationComponentType, animationComponent);
        ecsInterface.getSliceBoard().publishMessage(ECSUtil.makeSetComponentMessage(order));

        return true;
    }
}
