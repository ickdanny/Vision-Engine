package internalconfig.game.systems.menusystems.lockconditionhandlers;

import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.entity.EntityHandle;
import ecs.system.criticalorders.RemoveComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.LockConditions;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.components.ComponentTypes.*;

abstract class AbstractRemoveOnConditionHandler extends AbstractLockConditionHandlerTemplate {

    private final AbstractComponentType<LockConditions> lockConditionComponentType;

    AbstractRemoveOnConditionHandler(AbstractComponentTypeContainer componentTypeContainer){
        lockConditionComponentType = componentTypeContainer.getTypeInstance(LockConditionComponentType.class);
    }

    protected void removeLockCondition(AbstractPublishSubscribeBoard sliceBoard, EntityHandle handle){
        sliceBoard.publishMessage(
                ECSUtil.makeRemoveComponentMessage(new RemoveComponentOrder(handle, lockConditionComponentType))
        );
    }
}
