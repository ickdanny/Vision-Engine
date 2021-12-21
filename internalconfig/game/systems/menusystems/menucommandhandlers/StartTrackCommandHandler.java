package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.MenuCommands;
import internalconfig.game.systems.Topics;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.game.components.MenuCommands.START_TRACK;

class StartTrackCommandHandler implements AbstractMenuCommandHandler {

    private final AbstractComponentType<String> buttonActionType;

    StartTrackCommandHandler(AbstractComponentTypeContainer componentTypeContainer) {
        buttonActionType = componentTypeContainer.getTypeInstance(ComponentTypes.ButtonActionType.class);
    }

    @Override
    public MenuCommands getCommand() {
        return START_TRACK;
    }

    @Override
    public boolean handleCommand(AbstractECSInterface ecsInterface,
                                 AbstractMenuNavigationSystemInstance menuNavigationSystemInstance) {

        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle selectedElement = menuNavigationSystemInstance.getSelectedElement();

        if(dataStorage.containsComponent(selectedElement, buttonActionType)){
            String trackID = dataStorage.getComponent(selectedElement, buttonActionType);

            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            sliceBoard.publishMessage(new Message<>(Topics.MUSIC, trackID, Message.AGELESS));
        }
        else{
            throw new RuntimeException("trying to start track without ActionComponent");
        }

        return true;
    }
}
