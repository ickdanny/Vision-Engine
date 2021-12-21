package internalconfig.game.systems.graphicssystems;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSingleInstanceSystem;
import ecs.system.criticalorders.RemoveComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DrawOrder;
import internalconfig.game.components.SpriteInstruction;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.awt.image.BufferedImage;

import static internalconfig.game.components.ComponentTypes.*;

public class SpriteRemovalSystem extends AbstractSingleInstanceSystem<Double> {

    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
    private final AbstractComponentType<Void> visibleMarker;
    private final AbstractComponentType<DrawOrder> drawOrderComponentType;
    private final AbstractComponentType<BufferedImage> spriteComponentType;

    public SpriteRemovalSystem(AbstractComponentTypeContainer componentTypeContainer){
        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(SpriteInstructionComponentType.class);
        visibleMarker = componentTypeContainer.getTypeInstance(VisibleMarker.class);
        drawOrderComponentType = componentTypeContainer.getTypeInstance(DrawOrderComponentType.class);
        spriteComponentType = componentTypeContainer.getTypeInstance(SpriteComponentType.class);
    }

    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        removeSpriteIfComponentOfTypeWasRemoved(dataStorage, sliceBoard, spriteInstructionComponentType);
        removeSpriteIfComponentOfTypeWasRemoved(dataStorage, sliceBoard, visibleMarker);
        removeSpriteIfComponentOfTypeWasRemoved(dataStorage, sliceBoard, drawOrderComponentType);

        sliceBoard.ageAndCullMessages();
    }

    private void removeSpriteIfComponentOfTypeWasRemoved(AbstractDataStorage dataStorage,
                                                         AbstractPublishSubscribeBoard sliceBoard,
                                                         AbstractComponentType<?> componentType){
        for(Message<EntityHandle> message :
                sliceBoard.getMessageList(componentType.getRemoveComponentTopic())){

            EntityHandle handle = message.getMessage();
            if(
                    dataStorage.isAlive(handle)
                            && !dataStorage.containsComponent(handle, componentType)//edge case remove and add same tick
                            && dataStorage.containsComponent(handle, spriteComponentType)
            ){
                sliceBoard.publishMessage(ECSUtil.makeRemoveComponentMessage(
                        new RemoveComponentOrder(handle, spriteComponentType))
                );
            }
        }
    }
}
