package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystemInstance;

public interface AbstractMenuNavigationSystemInstance extends AbstractSystemInstance<Double> {
    EntityHandle getSelectedElement();
    void setSelectedElement(AbstractECSInterface ecsInterface, EntityHandle selectedElement);
}
