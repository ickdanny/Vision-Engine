package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import internalconfig.PropertiesUtil;
import internalconfig.game.components.MenuCommands;
import util.observer.AbstractSubject;

import java.util.Properties;

import static internalconfig.InternalProperties.FULLSCREEN;

import static internalconfig.game.components.MenuCommands.FULLSCREEN_TOGGLE;

class FullscreenToggleCommandHandler implements AbstractMenuCommandHandler {

    private final Properties properties;
    private final AbstractSubject fullscreenToggleBroadcaster;

    FullscreenToggleCommandHandler(Properties properties, AbstractSubject fullscreenToggleBroadcaster) {
        this.properties = properties;
        this.fullscreenToggleBroadcaster = fullscreenToggleBroadcaster;
    }

    @Override
    public MenuCommands getCommand() {
        return FULLSCREEN_TOGGLE;
    }

    @Override
    public boolean handleCommand(AbstractECSInterface ecsInterface,
                                 AbstractMenuNavigationSystemInstance menuNavigationSystemInstance) {
        fullscreenToggleBroadcaster.broadcast();
        PropertiesUtil.toggleBooleanProperty(properties, FULLSCREEN.getPropertyName());
        return true;
    }
}