package internalconfig.game.systems.menusystems.lockconditionhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.LockConditions;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.Iterator;
import java.util.List;

import static internalconfig.game.components.LockConditions.UNLOCK_WHEN_GAME_BEATEN;
import static internalconfig.game.GlobalTopics.*;

class UnlockWhenGameBeatenConditionHandler extends AbstractRemoveOnConditionHandler{

    public UnlockWhenGameBeatenConditionHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer);
    }

    @Override
    public LockConditions getCommand() {
        return UNLOCK_WHEN_GAME_BEATEN;
    }

    @Override
    public void handleCondition(AbstractECSInterface ecsInterface, EntityHandle handle) {
        AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
        if(globalBoard.hasTopicalMessages(GAME_BEATEN)){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            publishLockState(sliceBoard, dataStorage, handle, false);
            removeLockCondition(sliceBoard, handle);

            removeExtraGameBeatenMessages(globalBoard);
        }
    }

    private void removeExtraGameBeatenMessages(AbstractPublishSubscribeBoard globalBoard){
        List<Message<Void>> messageList = globalBoard.getMessageList(GAME_BEATEN);
        if(messageList.size() > 1){
            Iterator<Message<Void>> itr = messageList.iterator();
            itr.next();
            while(itr.hasNext()){
                itr.next();
                itr.remove();
            }
        }
    }
}
