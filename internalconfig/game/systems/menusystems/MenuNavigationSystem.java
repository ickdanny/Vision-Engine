package internalconfig.game.systems.menusystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.component.componentstorage.AbstractGroup;
import ecs.component.componentstorage.ComponentIterator;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.menusystems.menucommandhandlers.AbstractMenuNavigationSystemInstance;
import internalconfig.game.systems.menusystems.menucommandhandlers.MenuCommandHandlers;
import internalconfig.game.components.MenuCommands;
import resource.Resource;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;
import util.observer.AbstractSubject;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.systems.Topics.*;
import static internalconfig.game.systems.menusystems.ButtonSelectionState.*;

public class MenuNavigationSystem implements AbstractSystem<Double> {

    private final AbstractComponentType<MenuCommands> upCommandComponentType;
    private final AbstractComponentType<MenuCommands> downCommandComponentType;
    private final AbstractComponentType<MenuCommands> leftCommandComponentType;
    private final AbstractComponentType<MenuCommands> rightCommandComponentType;
    private final AbstractComponentType<MenuCommands> selectCommandComponentType;

    private final AbstractComponentType<Void> defaultSelectedElementMarker;

    private final MenuCommandHandlers handlers;

    public MenuNavigationSystem(Resource<Properties> propertiesResource,
                                AbstractComponentTypeContainer componentTypeContainer,
                                AbstractSubject cleanupBroadcaster,
                                AbstractSubject fullscreenToggleBroadcaster,
                                AbstractSubject muteToggleBroadcaster) {

        upCommandComponentType = componentTypeContainer.getTypeInstance(MenuCommandComponentType_Up.class);
        downCommandComponentType = componentTypeContainer.getTypeInstance(MenuCommandComponentType_Down.class);
        leftCommandComponentType = componentTypeContainer.getTypeInstance(MenuCommandComponentType_Left.class);
        rightCommandComponentType = componentTypeContainer.getTypeInstance(MenuCommandComponentType_Right.class);
        selectCommandComponentType = componentTypeContainer.getTypeInstance(MenuCommandComponentType_Select.class);

        defaultSelectedElementMarker = componentTypeContainer.getTypeInstance(DefaultSelectedElementMarker.class);

        handlers = new MenuCommandHandlers(
                propertiesResource,
                componentTypeContainer,
                cleanupBroadcaster,
                fullscreenToggleBroadcaster,
                muteToggleBroadcaster
        );
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractMenuNavigationSystemInstance {

        private MenuCommands keyboardBackCommand;
        private EntityHandle selectedElement;

        private Instance(){
            keyboardBackCommand = null;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            if(selectedElement == null){
                initSelectedElement(ecsInterface);
            }
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            if(keyboardBackCommand == null){
                initKeyboardBack(sliceBoard);
            }
            List<Message<MenuNavigationCommands>> menuNavigationCommands =
                    sliceBoard.getMessageList(MENU_NAVIGATION_COMMANDS);
            if(!menuNavigationCommands.isEmpty()){
                for(Message<MenuNavigationCommands> message : menuNavigationCommands){
                    if(!parseNavigationCommand(ecsInterface, message.getMessage())){
                        break;
                    }
                }
            }
            menuNavigationCommands.clear();
            sliceBoard.ageAndCullMessages();
        }

        private void initSelectedElement(AbstractECSInterface ecsInterface){
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            AbstractGroup group = dataStorage.createGroup(defaultSelectedElementMarker);
            ComponentIterator<?> itr = group.getComponentIterator(defaultSelectedElementMarker);
            if(!itr.hasNext()){
                throw new RuntimeException("Iterator for defaultSelectedElementMarker has zero elements");
            }
            itr.next();
            int id = itr.entityIDOfPreviousComponent();
            setSelectedElement(ecsInterface, dataStorage.makeHandle(id));
        }

        private void initKeyboardBack(AbstractPublishSubscribeBoard sliceBoard){
            try {
                keyboardBackCommand = sliceBoard.hasTopicalMessages(SPECIAL_KEYBOARD_BACK)
                        ? sliceBoard.getMessageList(SPECIAL_KEYBOARD_BACK).iterator().next().getMessage()
                        : MenuCommands.BACK;
            }
            catch(NoSuchElementException nsee){
                throw new RuntimeException("cannot find SPECIAL_KEYBOARD_BACK menu command", nsee);
            }
        }

        @Override
        public void setSelectedElement(AbstractECSInterface ecsInterface, EntityHandle selectedElement){
            if(this.selectedElement != selectedElement){
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

                if(this.selectedElement != null) {
                    sliceBoard.publishMessage(new Message<>(
                            BUTTON_SELECTION,
                            new ButtonSelectionMessage(this.selectedElement, UNSELECTED),
                            dataStorage.getMessageLifetime()
                    ));
                }

                this.selectedElement = selectedElement;
                ecsInterface.getSliceBoard().publishMessage(new Message<>(
                        BUTTON_SELECTION,
                        new ButtonSelectionMessage(selectedElement, SELECTED),
                        dataStorage.getMessageLifetime()
                ));
            }
        }

        //false if slice-critical
        private boolean parseNavigationCommand(AbstractECSInterface ecsInterface, MenuNavigationCommands navCommand){
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            MenuCommands menuCommand = null;
            switch(navCommand){
                case BACK:
                    menuCommand = keyboardBackCommand;
                    break;
                case SELECT:
                    menuCommand = getMenuCommandOfSelectedElementOfType(dataStorage, selectCommandComponentType);
                    break;
                case UP:
                    menuCommand = getMenuCommandOfSelectedElementOfType(dataStorage, upCommandComponentType);
                    break;
                case DOWN:
                    menuCommand = getMenuCommandOfSelectedElementOfType(dataStorage, downCommandComponentType);
                    break;
                case LEFT:
                    menuCommand = getMenuCommandOfSelectedElementOfType(dataStorage, leftCommandComponentType);
                    break;
                case RIGHT:
                    menuCommand = getMenuCommandOfSelectedElementOfType(dataStorage, rightCommandComponentType);
                    break;
            }
            return menuCommand == null || parseMenuCommand(ecsInterface, menuCommand);
        }

        private MenuCommands getMenuCommandOfSelectedElementOfType(AbstractDataStorage dataStorage,
                                                                   AbstractComponentType<MenuCommands> commandType){
            if(dataStorage.containsComponent(selectedElement, commandType)){
                return dataStorage.getComponent(selectedElement, commandType);
            }
            return null;
        }

        //false if slice critical
        private boolean parseMenuCommand(AbstractECSInterface ecsInterface,
                                         MenuCommands command){
            return handlers.handleCommand(ecsInterface, this, command);
        }

        @Override
        public EntityHandle getSelectedElement() {
            return selectedElement;
        }
    }
}
