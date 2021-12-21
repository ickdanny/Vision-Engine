package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSingleInstanceSystem;
import internalconfig.game.systems.SliceCodes;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static ecs.ECSTopics.PUSH_NEW_SLICE;
import static internalconfig.game.GlobalTopics.DIALOGUE_CODE;
import static internalconfig.game.GlobalTopics.TOP_LEVEL_SLICES;
import static internalconfig.game.systems.Topics.DIALOGUE_ENTRY;

public class DialogueScreenEntrySystem extends AbstractSingleInstanceSystem<Double> {
    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        List<Message<String>> messageList = sliceBoard.getMessageList(DIALOGUE_ENTRY);
        if(messageList.size() > 1){
            throw new RuntimeException("trying to enter >1 dialogue at once!");
        }

        if(messageList.size() == 1){
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            Message<String> message = messageList.iterator().next();
            String dialogueCode = message.getMessage();

            sliceBoard.publishMessage(new Message<>(PUSH_NEW_SLICE, SliceCodes.DIALOGUE, Message.AGELESS));
            globalBoard.publishMessage(new Message<>(TOP_LEVEL_SLICES, SliceCodes.DIALOGUE, Message.AGELESS));
            globalBoard.publishMessage(new Message<>(DIALOGUE_CODE, dialogueCode, Message.AGELESS));
        }

        sliceBoard.ageAndCullMessages();
    }
}