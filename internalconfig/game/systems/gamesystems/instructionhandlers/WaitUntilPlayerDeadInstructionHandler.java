package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.Instructions;
import internalconfig.game.systems.gamesystems.PlayerStateSystem;

class WaitUntilPlayerDeadInstructionHandler extends AbstractWaitPlayerStateInstructionHandler {
    WaitUntilPlayerDeadInstructionHandler() {
        super(PlayerStateSystem.States.DEAD);
    }

    @Override
    public Instructions<Void, Void> getInstruction() {
        return Instructions.WAIT_UNTIL_PLAYER_DEAD;
    }
}
