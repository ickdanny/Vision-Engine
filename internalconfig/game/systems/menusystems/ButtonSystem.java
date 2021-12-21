package internalconfig.game.systems.menusystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSingleInstanceSystem;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ButtonData;
import internalconfig.game.components.SpriteInstruction;
import util.math.geometry.TwoFramePosition;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.systems.Topics.*;

public class ButtonSystem extends AbstractSingleInstanceSystem<Double> {

    private final AbstractComponentType<ButtonData> buttonComponentType;
    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
    private final AbstractComponentType<TwoFramePosition> positionComponentType;

    public ButtonSystem(AbstractComponentTypeContainer componentTypeContainer) {
        buttonComponentType = componentTypeContainer.getTypeInstance(ButtonComponentType.class);
        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(SpriteInstructionComponentType.class);
        positionComponentType = componentTypeContainer.getTypeInstance(PositionComponentType.class);
    }

    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        if (sliceBoard.hasTopicalMessages(BUTTON_SELECTION)) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            List<Message<ButtonSelectionMessage>> buttonSelectionMessages =
                    sliceBoard.getMessageList(BUTTON_SELECTION);
            for (Message<ButtonSelectionMessage> message : buttonSelectionMessages) {
                updateButton(dataStorage, message.getMessage());
            }
        }

        sliceBoard.ageAndCullMessages();
    }

    private void updateButton(AbstractDataStorage dataStorage, ButtonSelectionMessage selectionMessage) {
        switch (selectionMessage.getState()) {
            case SELECTED:
                selectButton(dataStorage, selectionMessage.getButton());
                break;
            case UNSELECTED:
                unselectButton(dataStorage, selectionMessage.getButton());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + selectionMessage.getState());
        }
    }

    private void selectButton(AbstractDataStorage dataStorage, EntityHandle button) {
        if (isEntityInvalid(dataStorage, button)) {
            throw new RuntimeException("bad button!");
        }
        ButtonData buttonData = dataStorage.getComponent(button, buttonComponentType);
        dataStorage.getComponent(button, spriteInstructionComponentType).setImage(buttonData.getSelImage());
        dataStorage.getComponent(button, positionComponentType).getPos().setAs(buttonData.getSelPos());
    }

    private void unselectButton(AbstractDataStorage dataStorage, EntityHandle button) {
        if (isEntityInvalid(dataStorage, button)) {
            throw new RuntimeException("bad button!");
        }
        ButtonData buttonData = dataStorage.getComponent(button, buttonComponentType);
        dataStorage.getComponent(button, spriteInstructionComponentType).setImage(buttonData.getUnselImage());
        dataStorage.getComponent(button, positionComponentType).getPos().setAs(buttonData.getUnselPos());
    }

    boolean isEntityInvalid(AbstractDataStorage dataStorage, EntityHandle button) {
        return !dataStorage.containsAllComponents(button,
                buttonComponentType, spriteInstructionComponentType, positionComponentType);
    }
}
