package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.Instructions;
import internalconfig.game.systems.gamesystems.PlayerStateSystem;

class WaitUntilPlayerBombingInstructionHandler extends AbstractWaitPlayerStateInstructionHandler {
    WaitUntilPlayerBombingInstructionHandler() {
        super(PlayerStateSystem.States.BOMBING);
    }

    @Override
    public Instructions<Void, Void> getInstruction() {
        return Instructions.WAIT_UNTIL_PLAYER_BOMBING;
    }
}
