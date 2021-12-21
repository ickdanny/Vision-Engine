package internalconfig.game.systems.menusystems.lockconditionhandlers;

import ecs.AbstractECSInterface;
import ecs.entity.EntityHandle;
import internalconfig.game.components.LockConditions;

interface AbstractLockConditionHandler {
    @SuppressWarnings("SameReturnValue")
    LockConditions getCommand();
    void handleCondition(AbstractECSInterface ecsInterface, EntityHandle handle);
}
