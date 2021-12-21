package internalconfig.game.systems.menusystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSingleInstanceSystem;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ButtonData;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.SpriteInstruction;
import util.math.geometry.TwoFramePosition;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;
import util.tuple.Tuple2;

import java.util.List;

import static internalconfig.game.systems.Topics.NEW_LOCK_STATES;

public class LockingSystem extends AbstractSingleInstanceSystem<Double> {

    private final AbstractComponentType<ButtonData> buttonComponentType;
    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
    private final AbstractComponentType<TwoFramePosition> positionComponentType;

    public LockingSystem(AbstractComponentTypeContainer componentTypeContainer) {
        buttonComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.ButtonComponentType.class);
        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.SpriteInstructionComponentType.class);
        positionComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.PositionComponentType.class);
    }

    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        if (sliceBoard.hasTopicalMessages(NEW_LOCK_STATES)) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            List<Message<Tuple2<EntityHandle, Boolean>>> newLockStateMessages = sliceBoard.getMessageList(NEW_LOCK_STATES);
            for (Message<Tuple2<EntityHandle, Boolean>> message : newLockStateMessages) {
                Tuple2<EntityHandle, Boolean> tuple = message.getMessage();
                EntityHandle handle = tuple.a;
                if(tuple.b){
                    lockButton(dataStorage, handle);
                }
                else{
                    unlockButton(dataStorage, handle);
                }
            }
        }

        sliceBoard.ageAndCullMessages();
    }

    private void lockButton(AbstractDataStorage dataStorage, EntityHandle button){
        if (isEntityInvalid(dataStorage, button)) {
            throw new RuntimeException("bad button!");
        }
        ButtonData buttonData = dataStorage.getComponent(button, buttonComponentType);
        dataStorage.getComponent(button, spriteInstructionComponentType).setImage(buttonData.getLockedImage());
        dataStorage.getComponent(button, positionComponentType).getPos().setAs(buttonData.getUnselPos());

        buttonData.setLocked(true);
    }

    private void unlockButton(AbstractDataStorage dataStorage, EntityHandle button){
        if (isEntityInvalid(dataStorage, button)) {
            throw new RuntimeException("bad button!");
        }
        ButtonData buttonData = dataStorage.getComponent(button, buttonComponentType);
        dataStorage.getComponent(button, spriteInstructionComponentType).setImage(buttonData.getUnselImage());
        dataStorage.getComponent(button, positionComponentType).getPos().setAs(buttonData.getUnselPos());

        buttonData.setLocked(false);
    }

    boolean isEntityInvalid(AbstractDataStorage dataStorage, EntityHandle button) {
        return !dataStorage.containsAllComponents(button,
                buttonComponentType, spriteInstructionComponentType, positionComponentType);
    }
}
