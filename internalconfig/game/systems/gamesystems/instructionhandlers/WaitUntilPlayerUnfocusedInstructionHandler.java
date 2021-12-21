package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import internalconfig.game.systems.gamesystems.GameUtil;

import static internalconfig.game.components.Instructions.WAIT_UNTIL_PLAYER_UNFOCUSED;

class WaitUntilPlayerUnfocusedInstructionHandler implements AbstractInstructionHandler<Void, Void> {
    @Override
    public Instructions<Void, Void> getInstruction() {
        return WAIT_UNTIL_PLAYER_UNFOCUSED;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Void, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        return !GameUtil.isPlayerFocused(ecsInterface.getSliceBoard());
    }
}
