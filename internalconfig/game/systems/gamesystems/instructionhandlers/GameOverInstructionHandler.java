package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.game.systems.Topics.GAME_OVER;

class GameOverInstructionHandler implements AbstractInstructionHandler<Void, Void> {
    @Override
    public Instructions<Void, Void> getInstruction() {
        return Instructions.GAME_OVER;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface, InstructionNode<Void, Void> node, InstructionDataMap dataMap, int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        int messageLifetime = dataStorage.getMessageLifetime();
        sliceBoard.publishMessage(new Message<>(GAME_OVER, null, messageLifetime));
        return true;
    }
}