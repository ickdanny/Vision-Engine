package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.MenuCommands;
import resource.Resource;
import util.observer.AbstractSubject;

import java.util.EnumMap;
import java.util.Properties;

public class MenuCommandHandlers {

    private final EnumMap<MenuCommands, AbstractMenuCommandHandler> handlers;

    public MenuCommandHandlers(Resource<Properties> propertiesResource,
                               AbstractComponentTypeContainer componentTypeContainer,
                               AbstractSubject cleanupBroadcaster,
                               AbstractSubject fullscreenToggleBroadcaster,
                               AbstractSubject muteToggleBroadcaster) {
        handlers = new EnumMap<>(MenuCommands.class);
        makeHandlers(
                propertiesResource,
                componentTypeContainer,
                cleanupBroadcaster,
                fullscreenToggleBroadcaster,
                muteToggleBroadcaster
        );
        throwIfHandlersIncludesNull();
    }

    private void makeHandlers(Resource<Properties> propertiesResource,
                              AbstractComponentTypeContainer componentTypeContainer,
                              AbstractSubject cleanupBroadcaster,
                              AbstractSubject fullscreenToggleBroadcaster,
                              AbstractSubject muteToggleBroadcaster) {

        Properties properties = propertiesResource.getData();

        for (AbstractMenuCommandHandler handler : new AbstractMenuCommandHandler[]{
                new NavUpCommandHandler(componentTypeContainer),
                new NavDownCommandHandler(componentTypeContainer),
                new NavLeftCommandHandler(componentTypeContainer),
                new NavRightCommandHandler(componentTypeContainer),

                new NavFarUpCommandHandler(componentTypeContainer),
                new NavFarDownCommandHandler(componentTypeContainer),
                new NavFarLeftCommandHandler(componentTypeContainer),
                new NavFarRightCommandHandler(componentTypeContainer),


                new EnterCommandHandler(componentTypeContainer),
                new EnterAndStopMusicCommandHandler(componentTypeContainer),

                new BackCommandHandler(),
                new BackToMenuCommandHandler(),
                new BackToMainCommandHandler(),
                new BackAndWritePropertiesCommandHandler(propertiesResource),
                new BackAndSetTrackToMenuCommandHandler(),

                new StartTrackCommandHandler(componentTypeContainer),

                new SoundToggleCommandHandler(properties, muteToggleBroadcaster),
                new FullscreenToggleCommandHandler(properties, fullscreenToggleBroadcaster),

                new RestartGameCommandHandler(),
                new GameOverCommandHandler(),
                new ExitCommandHandler(cleanupBroadcaster),

                new NavDownAndSetShotBCommandHandler(componentTypeContainer),
                new NavDownAndSetShotCCommandHandler(componentTypeContainer),
                new NavUpAndSetShotCCommandHandler(componentTypeContainer),
                new NavUpAndSetShotACommandHandler(componentTypeContainer),
        }) {
            handlers.put(handler.getCommand(), handler);
        }
    }

    private void throwIfHandlersIncludesNull() {
        for (MenuCommands menuCommand : MenuCommands.values()) {
            if (handlers.get(menuCommand) == null) {
                throw new RuntimeException("handler for " + menuCommand + " is null");
            }
        }
    }

    public boolean handleCommand(AbstractECSInterface ecsInterface,
                                 AbstractMenuNavigationSystemInstance menuNavigationSystemInstance,
                                 MenuCommands menuCommand) {

        AbstractMenuCommandHandler handler = handlers.get(menuCommand);
        if (handler == null) {
            throw new RuntimeException("cannot find handler for " + menuCommand);
        }
        return handler.handleCommand(ecsInterface, menuNavigationSystemInstance);
    }
}
