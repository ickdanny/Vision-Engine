package internalconfig.game.systems.menusystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.game.GlobalTopics.*;
import static internalconfig.game.systems.Topics.*;

public class MenuTrackStarterSystem implements AbstractSystem<Double> {

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private static class Instance implements AbstractSystemInstance<Double> {

        public Instance() {
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            if (globalBoard.hasTopicalMessages(RETURN_TO_MENU)) {
                globalBoard.getMessageList(RETURN_TO_MENU).clear();
                sliceBoard.publishMessage(new Message<>(MUSIC, "01", Message.AGELESS));
            }

            sliceBoard.ageAndCullMessages();
        }
    }
}
