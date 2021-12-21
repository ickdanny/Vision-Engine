package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.SliceUtil;
import internalconfig.game.components.MenuCommands;
import resource.Resource;

import java.util.Properties;

import static internalconfig.game.components.MenuCommands.BACK_AND_WRITE_PROPERTIES;

class BackAndWritePropertiesCommandHandler implements AbstractMenuCommandHandler {

    private final Resource<Properties> propertiesResource;

    BackAndWritePropertiesCommandHandler(Resource<Properties> propertiesResource){
        this.propertiesResource = propertiesResource;
    }

    @Override
    public MenuCommands getCommand() {
        return BACK_AND_WRITE_PROPERTIES;
    }

    @Override
    public boolean handleCommand(AbstractECSInterface ecsInterface,
                                 AbstractMenuNavigationSystemInstance menuNavigationSystemInstance) {
        SliceUtil.back(ecsInterface);
        propertiesResource.writeData();
        return false;
    }
}
