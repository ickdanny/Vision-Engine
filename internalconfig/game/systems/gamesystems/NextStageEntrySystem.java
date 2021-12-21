package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSingleInstanceSystem;
import internalconfig.game.SliceUtil;
import internalconfig.game.systems.GameConfigObject;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static ecs.ECSTopics.PUSH_NEW_SLICE;
import static internalconfig.game.GlobalTopics.GAME_CONFIG_OBJECT;
import static internalconfig.game.GlobalTopics.TOP_LEVEL_SLICES;
import static internalconfig.game.systems.SliceCodes.GAME;
import static internalconfig.game.systems.SliceCodes.LOAD;
import static internalconfig.game.systems.Topics.NEXT_STAGE_ENTRY;

public class NextStageEntrySystem extends AbstractSingleInstanceSystem<Double> {
    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        if(!sliceBoard.hasTopicalMessages(PUSH_NEW_SLICE)) { //don't want to fuck up pause entry

            List<Message<Void>> messageList = sliceBoard.getMessageList(NEXT_STAGE_ENTRY);
            if (messageList.size() > 1) {
                throw new RuntimeException("trying to enter >1 next stages at once!");
            }

            if (messageList.size() == 1) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

                GameConfigObject oldConfig = GameUtil.getGameConfigObject(globalBoard);

                switch (oldConfig.getGameMode()) {
                    case STORY:
                        enterNextStage(ecsInterface, sliceBoard, globalBoard, oldConfig);
                        break;
                    case PRACTICE:
                        SliceUtil.returnToMenu(ecsInterface);
                        break;
                    case EXTRA:
                        throw new RuntimeException("trying to enter next stage on Extra stage!");
                    default:
                        throw new RuntimeException("unrecognized gamemode: " + oldConfig.getGameMode());
                }

            }
        }
        sliceBoard.ageAndCullMessages();
    }

    private void enterNextStage(AbstractECSInterface ecsInterface,
                                AbstractPublishSubscribeBoard sliceBoard,
                                AbstractPublishSubscribeBoard globalBoard,
                                GameConfigObject oldConfig){

        int stage = oldConfig.getStage();

        if(stage >= 6){ //extra = 7
            throw new RuntimeException("trying to enter next stage on " + stage);
        }

        List<Message<GameConfigObject>> gameConfigObjectMessageList = globalBoard.getMessageList(GAME_CONFIG_OBJECT);
        gameConfigObjectMessageList.clear();

        GameConfigObject newConfig = makeNextStageGameConfigObject(oldConfig);
        gameConfigObjectMessageList.add(new Message<>(GAME_CONFIG_OBJECT, newConfig, Message.AGELESS));

        SliceUtil.returnToMenu(ecsInterface); //this handles removing the TOP_LEVEL_SLICES messages

        sliceBoard.publishMessage(new Message<>(PUSH_NEW_SLICE, GAME, Message.AGELESS));
//        sliceBoard.publishMessage(new Message<>(Topics.MUSIC, MusicSystem.RESET_CODE, Message.AGELESS));

        ecsInterface.getGlobalBoard().publishMessage(
                new Message<>(TOP_LEVEL_SLICES, GAME, Message.AGELESS));

        sliceBoard.publishMessage(new Message<>(PUSH_NEW_SLICE, LOAD, Message.AGELESS));

        ecsInterface.getGlobalBoard().publishMessage(
                new Message<>(TOP_LEVEL_SLICES, LOAD, Message.AGELESS));
    }

    private GameConfigObject makeNextStageGameConfigObject(GameConfigObject oldGameConfig){
        return oldGameConfig.setStage(oldGameConfig.getStage() + 1);
    }
}