package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.MenuCommands;

interface AbstractMenuCommandHandler {
    MenuCommands getCommand();
    //return false if slice-critical
    boolean handleCommand(AbstractECSInterface ecsInterface,
                          AbstractMenuNavigationSystemInstance menuNavigationSystemInstance);
}