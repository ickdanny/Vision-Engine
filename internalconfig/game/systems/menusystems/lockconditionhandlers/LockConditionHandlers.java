package internalconfig.game.systems.menusystems.lockconditionhandlers;

import ecs.AbstractECSInterface;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.LockConditions;

import java.util.EnumMap;

public class LockConditionHandlers {
    private final EnumMap<LockConditions, AbstractLockConditionHandler> handlers;

    public LockConditionHandlers(AbstractComponentTypeContainer componentTypeContainer) {
        handlers = new EnumMap<>(LockConditions.class);
        makeHandlers(componentTypeContainer);
        throwIfHandlersIncludesNull();
    }

    private void makeHandlers(AbstractComponentTypeContainer componentTypeContainer) {

        for (AbstractLockConditionHandler handler : new AbstractLockConditionHandler[]{
            new UnlockWhenGameBeatenConditionHandler(componentTypeContainer),
        }) {
            handlers.put(handler.getCommand(), handler);
        }
    }

    private void throwIfHandlersIncludesNull() {
        for (LockConditions lockCondition : LockConditions.values()) {
            if (handlers.get(lockCondition) == null) {
                throw new RuntimeException("handler for " + lockCondition + " is null");
            }
        }
    }

    public void handleCondition(AbstractECSInterface ecsInterface, EntityHandle handle, LockConditions lockCondition) {
        AbstractLockConditionHandler handler = handlers.get(lockCondition);
        if (handler == null) {
            throw new RuntimeException("cannot find handler for " + lockCondition);
        }
        handler.handleCondition(ecsInterface, handle);
    }
}
