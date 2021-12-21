package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.InternalProperties;
import internalconfig.PropertiesUtil;
import internalconfig.game.GameConfig;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import internalconfig.game.systems.Difficulty;
import internalconfig.game.systems.GameConfigObject;
import internalconfig.game.systems.GameMode;
import internalconfig.game.systems.PlayerData;
import internalconfig.game.systems.gamesystems.GameUtil;
import resource.Resource;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.Properties;

import static internalconfig.game.GlobalTopics.GAME_BEATEN;
import static internalconfig.game.systems.Topics.GAME_OVER;
import static internalconfig.game.systems.Topics.GAME_WIN;

class GameWinInstructionHandler implements AbstractInstructionHandler<Void, Void> {

    private final Resource<Properties> propertiesResource;

    GameWinInstructionHandler(Resource<Properties> propertiesResource){
        this.propertiesResource = propertiesResource;
    }

    @Override
    public Instructions<Void, Void> getInstruction() {
        return Instructions.GAME_WIN;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Void, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID){

        AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
        GameConfigObject gameConfigObject = GameUtil.getGameConfigObject(globalBoard);
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        int messageLifetime = dataStorage.getMessageLifetime();

        if(gameConfigObject.isValid()){
            if(gameConfigObject.getGameMode() == GameMode.STORY){
                PlayerData playerData = gameConfigObject.getPlayerData();
                if(playerData.getContinues() == GameConfig.INIT_CONTINUES){
                    sliceBoard.publishMessage(new Message<>(GAME_WIN, null, messageLifetime));

                    if(gameConfigObject.getDifficulty() != Difficulty.EASY) {
                        globalBoard.publishMessage(new Message<>(GAME_BEATEN, null, Message.AGELESS));
                        unlockExtra();
                    }
                    return true;
                }
            }
        } else{
            throw new RuntimeException("game config object not valid! " + gameConfigObject);
        }

        sliceBoard.publishMessage(new Message<>(GAME_OVER, null, messageLifetime));
        return true;
    }

    private void unlockExtra(){
        Properties properties = propertiesResource.getData();
        if(!PropertiesUtil.getBooleanProperty(properties, InternalProperties.EXTRA.getPropertyName())){
            PropertiesUtil.setBooleanProperty(properties, InternalProperties.EXTRA.getPropertyName(), true);
            propertiesResource.writeData();
        }
    }
}