package internalconfig.game.systems.gamesystems.instructionhandlers;

import internalconfig.game.components.Instructions;
import internalconfig.game.systems.gamesystems.PlayerStateSystem;

class WaitUntilPlayerRespawningInstructionHandler extends AbstractWaitPlayerStateInstructionHandler {
    WaitUntilPlayerRespawningInstructionHandler() {
        super(PlayerStateSystem.States.RESPAWNING);
    }

    @Override
    public Instructions<Void, Void> getInstruction() {
        return Instructions.WAIT_UNTIL_PLAYER_RESPAWNING;
    }
}
