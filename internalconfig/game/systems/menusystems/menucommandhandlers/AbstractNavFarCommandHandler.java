package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ButtonData;

import static internalconfig.game.components.ComponentTypes.*;

abstract class AbstractNavFarCommandHandler implements AbstractMenuCommandHandler {

    private final AbstractComponentType<EntityHandle> directionComponentType;
    private final AbstractComponentType<ButtonData> buttonComponentType;

    AbstractNavFarCommandHandler(AbstractComponentTypeContainer componentTypeContainer,
                                 Class<? extends AbstractComponentType<EntityHandle>>
                                              directionComponentType) {
        this.directionComponentType = componentTypeContainer.getTypeInstance(directionComponentType);
        buttonComponentType = componentTypeContainer.getTypeInstance(ButtonComponentType.class);
    }

    @Override
    public boolean handleCommand(AbstractECSInterface ecsInterface,
                                 AbstractMenuNavigationSystemInstance menuNavigationSystemInstance) {

        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle lastValidElement = menuNavigationSystemInstance.getSelectedElement();
        EntityHandle testElement = lastValidElement;
        while(dataStorage.containsComponent(testElement, directionComponentType)){
            testElement = dataStorage.getComponent(testElement, directionComponentType);
            if(!isLockedButton(dataStorage, testElement)){
                lastValidElement = testElement;
            }
        }

        menuNavigationSystemInstance.setSelectedElement(ecsInterface, lastValidElement);

        return true;
    }

    private boolean isLockedButton(AbstractDataStorage dataStorage, EntityHandle element){
        if(dataStorage.containsComponent(element, buttonComponentType)){
            ButtonData buttonData = dataStorage.getComponent(element, buttonComponentType);
            return buttonData.isLocked();
        }
        return false;
    }
}
