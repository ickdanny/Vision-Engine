package ecs;

import ecs.system.AbstractSingleInstanceSystem;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.Iterator;
import java.util.List;

import static ecs.ECSTopics.*;

public class SliceStackCriticalSystem<T> extends AbstractSingleInstanceSystem<T> {

        @Override
        public void run(AbstractECSInterface ecsInterface, T data) {
            doSliceStackOperations((AbstractECSInterface_PP)ecsInterface);
            ecsInterface.getSliceBoard().ageAndCullMessages();
        }

        private void doSliceStackOperations(AbstractECSInterface_PP ecsInterface){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            List<Message<String>> popList = sliceBoard.getMessageList(POP_SLICE_BACK_TO);
            List<Message<String>> pushList = sliceBoard.getMessageList(PUSH_NEW_SLICE);
            popSlices(ecsInterface, popList);
            pushSlices(ecsInterface, pushList);
        }

        private void popSlices(AbstractECSInterface_PP ecsInterface, List<Message<String>> popList){
            if(!popList.isEmpty()){
                if(popList.size() > 1){
                    throw new RuntimeException("more than one popSlice instruction in one tick: " + popList);
                }
                AbstractSliceStack sliceStack = ecsInterface.getSliceStack();
                Message<String> message = popList.remove(0);
                String sliceName = message.getMessage();
                sliceStack.popSliceBackTo(sliceName);
            }
        }

        private void pushSlices(AbstractECSInterface_PP ecsInterface, List<Message<String>> pushList){
            if(!pushList.isEmpty()) {
                AbstractSliceStack sliceStack = ecsInterface.getSliceStack();
                Iterator<Message<String>> itr = pushList.iterator();
                while (itr.hasNext()) {
                    Message<String> message = itr.next();
                    String sliceName = message.getMessage();
                    sliceStack.pushSlice(sliceName);
                    itr.remove();
                }
            }
        }
    }