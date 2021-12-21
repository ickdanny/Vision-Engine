package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.MenuCommands;
import util.observer.AbstractSubject;

import static internalconfig.game.components.MenuCommands.EXIT;

class ExitCommandHandler implements AbstractMenuCommandHandler {

    private final AbstractSubject cleanupBroadcaster;

    ExitCommandHandler(AbstractSubject cleanupBroadcaster) {
        this.cleanupBroadcaster = cleanupBroadcaster;
    }

    @Override
    public MenuCommands getCommand() {
        return EXIT;
    }

    @Override
    public boolean handleCommand(AbstractECSInterface ecsInterface,
                                 AbstractMenuNavigationSystemInstance menuNavigationSystemInstance) {
        cleanupBroadcaster.broadcast();
        return false;
    }
}
