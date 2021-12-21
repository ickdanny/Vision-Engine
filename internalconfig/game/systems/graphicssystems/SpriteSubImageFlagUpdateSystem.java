package internalconfig.game.systems.graphicssystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSingleInstanceSystem;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.SpriteInstruction;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.awt.*;

import static internalconfig.game.components.ComponentTypes.*;

public class SpriteSubImageFlagUpdateSystem extends AbstractSingleInstanceSystem<Double> {

    private final AbstractComponentType<Rectangle> spriteSubImageComponentType;
    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;

    public SpriteSubImageFlagUpdateSystem(AbstractComponentTypeContainer componentTypeContainer){
        spriteSubImageComponentType = componentTypeContainer.getTypeInstance(SpriteSubImageComponentType.class);
        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(SpriteInstructionComponentType.class);
    }

    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        for(Message<EntityHandle> message : sliceBoard.getMessageList(spriteSubImageComponentType.getRemoveComponentTopic())){
            EntityHandle handle = message.getMessage();
            flagUpdate(dataStorage, handle);
        }

        for(Message<EntityHandle> message : sliceBoard.getMessageList(spriteSubImageComponentType.getSetComponentTopic())){
            EntityHandle handle = message.getMessage();
            flagUpdate(dataStorage, handle);
        }

        sliceBoard.ageAndCullMessages();
    }

    private void flagUpdate(AbstractDataStorage dataStorage, EntityHandle handle){
        if(dataStorage.containsComponent(handle, spriteInstructionComponentType)){
            dataStorage.getComponent(handle, spriteInstructionComponentType).flagForUpdate();
        }
    }
}
