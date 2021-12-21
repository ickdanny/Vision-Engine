package internalconfig.game;

import ecs.AbstractECSInterface;
import internalconfig.game.systems.Topics;
import internalconfig.game.systems.soundsystems.MusicSystem;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static ecs.ECSTopics.POP_SLICE_BACK_TO;
import static internalconfig.game.GlobalTopics.RETURN_TO_MENU;
import static internalconfig.game.GlobalTopics.TOP_LEVEL_SLICES;
import static internalconfig.game.systems.SliceCodes.GAME;
import static internalconfig.game.systems.SliceCodes.MAIN;

public final class SliceUtil {

    public static void back(AbstractECSInterface ecsInterface){
        List<Message<String>> topLevelSlices = ecsInterface.getGlobalBoard().getMessageList(TOP_LEVEL_SLICES);
        int initSize = topLevelSlices.size();
        if(initSize > 1){
            topLevelSlices.remove(initSize - 1);
            String prevTopLevelSlice = topLevelSlices.get(initSize - 2).getMessage();
            Message<String> popSliceMessage = new Message<>(POP_SLICE_BACK_TO, prevTopLevelSlice, Message.AGELESS);
            ecsInterface.getSliceBoard().publishMessage(popSliceMessage);
        }
        else{
            throw new RuntimeException("back should have at least 2 top level slices");
        }
    }
    public static void returnToMain(AbstractECSInterface ecsInterface){
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        List<Message<String>> topLevelSlices = ecsInterface.getGlobalBoard().getMessageList(TOP_LEVEL_SLICES);
        int initSize = topLevelSlices.size();
        if(initSize > 1) {
            topLevelSlices.subList(1, initSize).clear();
            Message<String> popSliceMessage = new Message<>(POP_SLICE_BACK_TO, MAIN, Message.AGELESS);
            sliceBoard.publishMessage(popSliceMessage);
            sliceBoard.publishMessage(new Message<>(Topics.MUSIC, MusicSystem.RESET_CODE, Message.AGELESS));
            publishReturnToMenuMessage(ecsInterface);
        }
        else{
            throw new RuntimeException("returnToMain should have at least 2 top level slices");
        }
    }

    public static void returnToMenu(AbstractECSInterface ecsInterface){
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        List<Message<String>> topLevelSlices = ecsInterface.getGlobalBoard().getMessageList(TOP_LEVEL_SLICES);
        int initSize = topLevelSlices.size();
        if(initSize > 1){
            int indexOfGame = -1;
            for(int i = topLevelSlices.size() - 1; i >= 0; --i){
                if(topLevelSlices.get(i).getMessage().equals(GAME)){
                    indexOfGame = i;
                    break;
                }
            }
            if (topLevelSlices.size() > indexOfGame) {
                topLevelSlices.subList(indexOfGame, topLevelSlices.size()).clear();
            }
            String prevMainSlice = topLevelSlices.get(indexOfGame - 1).getMessage();
            Message<String> popSliceMessage = new Message<>(POP_SLICE_BACK_TO, prevMainSlice, Message.AGELESS);
            sliceBoard.publishMessage(popSliceMessage);
            sliceBoard.publishMessage(new Message<>(Topics.MUSIC, MusicSystem.RESET_CODE, Message.AGELESS));
            publishReturnToMenuMessage(ecsInterface);
        }
        else{
            throw new RuntimeException("returnToMenu should have at least 2 top level slices");
        }
    }

    private static void publishReturnToMenuMessage(AbstractECSInterface ecsInterface){
        ecsInterface.getGlobalBoard().publishMessage(new Message<>(RETURN_TO_MENU, null, Message.AGELESS));
    }
}