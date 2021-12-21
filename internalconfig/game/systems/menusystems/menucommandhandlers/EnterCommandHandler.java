package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.MenuCommands;
import internalconfig.game.systems.Topics;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static ecs.ECSTopics.PUSH_NEW_SLICE;
import static internalconfig.game.GlobalTopics.TOP_LEVEL_SLICES;
import static internalconfig.game.systems.SliceCodes.*;
import static internalconfig.game.systems.Topics.SLICE_ENTRY;
import static internalconfig.game.components.MenuCommands.ENTER;
import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.systems.soundsystems.MusicSystem.RESET_CODE;

class EnterCommandHandler implements AbstractMenuCommandHandler {

    private final AbstractComponentType<String> buttonActionType;

    EnterCommandHandler(AbstractComponentTypeContainer componentTypeContainer) {
        buttonActionType = componentTypeContainer.getTypeInstance(ButtonActionType.class);
    }

    @Override
    public MenuCommands getCommand() {
        return ENTER;
    }

    @Override
    public boolean handleCommand(AbstractECSInterface ecsInterface,
                                 AbstractMenuNavigationSystemInstance menuNavigationSystemInstance) {

        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle selectedElement = menuNavigationSystemInstance.getSelectedElement();

        if(dataStorage.containsComponent(selectedElement, buttonActionType)){
            String sliceCode = dataStorage.getComponent(selectedElement, buttonActionType);
            String sliceName = getSliceName(ecsInterface, sliceCode);
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            sliceBoard.publishMessage(new Message<>(PUSH_NEW_SLICE, sliceName, Message.AGELESS));
            ecsInterface.getGlobalBoard().publishMessage(
                    new Message<>(TOP_LEVEL_SLICES, sliceName, Message.AGELESS));
            //broadcast the code
            sliceBoard.publishMessage(new Message<>(SLICE_ENTRY, sliceCode, dataStorage.getMessageLifetime()));

            //unclean, but gotta do what I gotta do
            if(sliceName.equals(GAME)){
                sliceBoard.publishMessage(new Message<>(PUSH_NEW_SLICE, LOAD, Message.AGELESS));
                ecsInterface.getGlobalBoard().publishMessage(
                        new Message<>(TOP_LEVEL_SLICES, LOAD, Message.AGELESS));
                sliceBoard.publishMessage(new Message<>(SLICE_ENTRY, LOAD, dataStorage.getMessageLifetime()));
                sliceBoard.publishMessage(new Message<>(Topics.MUSIC, RESET_CODE, Message.AGELESS));
            }
        }
        else{
            throw new RuntimeException("trying to enter " + selectedElement + " without an enterSlice component");
        }

        return false;
    }

    private String getSliceName(AbstractECSInterface ecsInterface, String sliceCode){
        if(sliceCode.contains(STAGE)){
            String currentSliceName = getCurrentSliceName(ecsInterface);
            return currentSliceName + sliceCode.substring(STAGE.length()) + "_" + SHOT;
        }
        switch(sliceCode){
            case START:
            case EXTRA:
            case PRACTICE:
                return sliceCode + DIFFICULTY;
            case EASY:
            case MEDIUM:
            case HARD:
            case LUNATIC:
            case EXTRAD:
                String currentSliceName = getCurrentSliceName(ecsInterface);
                if(currentSliceName.contains(PRACTICE)){
                    return currentSliceName + sliceCode + STAGE;
                }
                else{
                    return currentSliceName + sliceCode + SHOT;
                }
            case SHOT + "A":
            case SHOT + "B":
            case SHOT + "C":
                return GAME;

            default: return sliceCode;
        }
    }

    private String getCurrentSliceName(AbstractECSInterface ecsInterface){
        List<Message<String>> topLevelSlices = ecsInterface.getGlobalBoard().getMessageList(TOP_LEVEL_SLICES);
        return topLevelSlices.get(topLevelSlices.size() - 1).getMessage();
    }


}
