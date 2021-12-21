package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import internalconfig.PropertiesUtil;
import internalconfig.game.components.MenuCommands;
import util.messaging.Message;
import util.observer.AbstractSubject;

import java.util.Properties;

import static internalconfig.InternalProperties.MUTE;
import static internalconfig.game.components.MenuCommands.SOUND_TOGGLE;
import static internalconfig.game.systems.Topics.MUSIC;

class SoundToggleCommandHandler implements AbstractMenuCommandHandler {

    private final Properties properties;
    private final AbstractSubject muteToggleBroadcaster;

    SoundToggleCommandHandler(Properties properties, AbstractSubject muteToggleBroadcaster) {
        this.properties = properties;
        this.muteToggleBroadcaster = muteToggleBroadcaster;
    }

    @Override
    public MenuCommands getCommand() {
        return SOUND_TOGGLE;
    }

    @Override
    public boolean handleCommand(AbstractECSInterface ecsInterface,
                                 AbstractMenuNavigationSystemInstance menuNavigationSystemInstance) {
        muteToggleBroadcaster.broadcast();
        boolean soundOff = PropertiesUtil.toggleBooleanProperty(properties, MUTE.getPropertyName());
        if(!soundOff){
            ecsInterface.getSliceBoard().publishMessage(new Message<>(MUSIC, "01", Message.AGELESS));
        }
        return true;
    }
}