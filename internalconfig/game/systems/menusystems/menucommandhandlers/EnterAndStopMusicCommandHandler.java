package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.MenuCommands;
import util.messaging.Message;

import static internalconfig.game.systems.Topics.*;
import static internalconfig.game.systems.soundsystems.MusicSystem.RESET_CODE;

class EnterAndStopMusicCommandHandler extends EnterCommandHandler {
    EnterAndStopMusicCommandHandler(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer);
    }

    @Override
    public MenuCommands getCommand() {
        return MenuCommands.ENTER_AND_STOP_MUSIC;
    }

    @Override
    public boolean handleCommand(AbstractECSInterface ecsInterface, AbstractMenuNavigationSystemInstance menuNavigationSystemInstance) {
        ecsInterface.getSliceBoard().publishMessage(new Message<>(MUSIC, RESET_CODE, Message.AGELESS));
        return super.handleCommand(ecsInterface, menuNavigationSystemInstance);
    }
}
