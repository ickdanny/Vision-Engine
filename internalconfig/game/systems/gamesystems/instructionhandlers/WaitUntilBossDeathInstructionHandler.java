package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.systems.Topics.BOSS_DEATH;

class WaitUntilBossDeathInstructionHandler implements AbstractInstructionHandler<Void, Void>{
    @Override
    public Instructions<Void, Void> getInstruction() {
        return Instructions.WAIT_UNTIL_BOSS_DEATH;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Void, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        return sliceBoard.hasTopicalMessages(BOSS_DEATH);
    }
}
