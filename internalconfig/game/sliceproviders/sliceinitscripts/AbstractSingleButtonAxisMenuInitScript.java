package internalconfig.game.sliceproviders.sliceinitscripts;

import ecs.ECSUtil;
import ecs.entity.EntityHandle;
import ecs.entity.NamedEntityHandle;
import ecs.system.criticalorders.AddEntityOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.graphicssystems.SpriteInstructionSystem;
import internalconfig.game.systems.graphicssystems.DrawCommandSystem;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static ecs.ECSTopics.*;

@SuppressWarnings("unused")
public abstract class AbstractSingleButtonAxisMenuInitScript extends AbstractMenuInitScript {

    private Message<AddEntityOrder>[] addButtonMessages;

    public AbstractSingleButtonAxisMenuInitScript(AbstractComponentTypeContainer componentTypeContainer,
                                                  SpriteInstructionSystem spriteInstructionSystem,
                                                  DrawCommandSystem drawCommandSystem) {
        super(componentTypeContainer, spriteInstructionSystem, drawCommandSystem);
    }

    @Override
    protected void publishOrders(AbstractPublishSubscribeBoard sliceBoard) {
        for(Message<AddEntityOrder> message : makeAddBackgroundMessages()){
            sliceBoard.publishMessage(message);
        }
        addButtonMessages = makeAddButtonMessages();
        for(Message<AddEntityOrder> message : addButtonMessages){
            sliceBoard.publishMessage(message);
        }
    }

    protected abstract Message<AddEntityOrder>[] makeAddBackgroundMessages();
    protected abstract Message<AddEntityOrder>[] makeAddButtonMessages();

    @Override
    protected void hookUpElements(AbstractPublishSubscribeBoard sliceBoard) {
        if(addButtonMessages == null){
            throw new RuntimeException("null buttonMessages!");
        }

        List<Message<NamedEntityHandle>> newNamedEntities = sliceBoard.getMessageList(NEW_NAMED_ENTITIES);

        EntityHandle buttonA = null;
        EntityHandle buttonB = null;
        for(int i = 0; i < addButtonMessages.length - 1; ++i){
            if(buttonA == null){
                buttonA = ECSUtil.getHandleForNamedEntity(addButtonMessages[i].getMessage().getName(), newNamedEntities);
            }
            else{
                buttonA = buttonB;
            }
            buttonB = ECSUtil.getHandleForNamedEntity(addButtonMessages[i+1].getMessage().getName(), newNamedEntities);
            hookTwoButtons(sliceBoard, buttonA, buttonB);
        }

        addButtonMessages = null;
    }
    protected abstract void hookTwoButtons(AbstractPublishSubscribeBoard sliceBoard,
                                           EntityHandle buttonA, EntityHandle buttonB);


}
