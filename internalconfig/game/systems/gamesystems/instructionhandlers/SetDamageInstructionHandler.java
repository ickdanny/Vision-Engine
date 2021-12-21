package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.criticalorders.SetComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.components.ComponentTypes.*;

class SetDamageInstructionHandler implements AbstractInstructionHandler<Integer, Void> {

    private final AbstractComponentType<Integer> damageComponentType;

    SetDamageInstructionHandler(AbstractComponentTypeContainer componentTypeContainer){
        damageComponentType = componentTypeContainer.getTypeInstance(DamageComponentType.class);
    }

    @Override
    public Instructions<Integer, Void> getInstruction() {
        return Instructions.SET_DAMAGE;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Integer, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);
        sliceBoard.publishMessage(ECSUtil.makeSetComponentMessage(
                new SetComponentOrder<>(handle, damageComponentType,  node.getData())
        ));

        return true;
    }
}
