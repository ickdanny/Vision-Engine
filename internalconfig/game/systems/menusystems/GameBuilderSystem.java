package internalconfig.game.systems.menusystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSingleInstanceSystem;
import internalconfig.game.systems.Difficulty;
import internalconfig.game.systems.GameConfigObject;
import internalconfig.game.systems.GameMode;
import internalconfig.game.systems.ShotType;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static internalconfig.game.systems.Topics.SLICE_ENTRY;
import static internalconfig.game.systems.SliceCodes.*;
import static internalconfig.game.GlobalTopics.GAME_CONFIG_OBJECT;

public class GameBuilderSystem extends AbstractSingleInstanceSystem<Double> {

    private GameConfigObject gameConfigObject;

    public GameBuilderSystem() {
        this.gameConfigObject = new GameConfigObject();
    }

    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        List<Message<String>> sliceEntryMessages = sliceBoard.getMessageList(SLICE_ENTRY);
        for(Message<String> sliceEntryMessage: sliceEntryMessages){
            String sliceCode = sliceEntryMessage.getMessage();
            parseSliceCode(sliceCode, ecsInterface);
        }
        sliceBoard.ageAndCullMessages();
    }

    private void parseSliceCode(String sliceCode, AbstractECSInterface ecsInterface){
        if(sliceCode.contains(STAGE)){
            int stage = Integer.parseInt(sliceCode.substring(STAGE.length()));
            gameConfigObject = gameConfigObject.setStage(stage);
        }
        else {
            switch (sliceCode) {
                case START:
                    gameConfigObject = gameConfigObject.setGameMode(GameMode.STORY);
                    break;
                case EXTRA:
                    gameConfigObject = gameConfigObject.setGameMode(GameMode.EXTRA);
                    break;
                case PRACTICE:
                    gameConfigObject = gameConfigObject.setGameMode(GameMode.PRACTICE);
                    break;
                case EASY:
                    gameConfigObject = gameConfigObject.setDifficulty(Difficulty.EASY);
                    break;
                case MEDIUM:
                    gameConfigObject = gameConfigObject.setDifficulty(Difficulty.MEDIUM);
                    break;
                case HARD:
                    gameConfigObject = gameConfigObject.setDifficulty(Difficulty.HARD);
                    break;
                case LUNATIC:
                    gameConfigObject = gameConfigObject.setDifficulty(Difficulty.LUNATIC);
                    break;
                case EXTRAD:
                    gameConfigObject = gameConfigObject.setDifficulty(Difficulty.EXTRA);
                    break;
                case SHOT + "A":
                    gameConfigObject = gameConfigObject.setShotType(ShotType.A);
                    pushGameConfig(ecsInterface);
                    break;
                case SHOT + "B":
                    gameConfigObject = gameConfigObject.setShotType(ShotType.B);
                    pushGameConfig(ecsInterface);
                    break;
                case SHOT + "C":
                    gameConfigObject = gameConfigObject.setShotType(ShotType.C);
                    pushGameConfig(ecsInterface);
                    break;
            }
        }
    }

    private void pushGameConfig(AbstractECSInterface ecsInterface){
        GameMode gameMode = gameConfigObject.getGameMode();
        if(gameMode == GameMode.STORY){
            gameConfigObject = gameConfigObject.setStage(1);
        }
        else if(gameMode == GameMode.EXTRA){
            gameConfigObject = gameConfigObject.setStage(7);
        }
        gameConfigObject = gameConfigObject.setRandom(System.currentTimeMillis());
        if(!gameConfigObject.isValid()){
            throw new RuntimeException("invalid game config: " + gameConfigObject);
        }

        gameConfigObject.loadPlayerData();

        AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
        globalBoard.getMessageList(GAME_CONFIG_OBJECT).clear();
        ecsInterface.getGlobalBoard().publishMessage(
                new Message<>(GAME_CONFIG_OBJECT, gameConfigObject, Message.AGELESS));
    }


}