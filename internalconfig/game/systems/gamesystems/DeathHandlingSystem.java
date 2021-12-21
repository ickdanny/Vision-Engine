package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSingleInstanceSystem;
import ecs.system.criticalorders.RemoveEntityOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DeathCommands;
import internalconfig.game.systems.gamesystems.deathhandlers.DeathHandlers;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static internalconfig.game.systems.Topics.DEATHS;
import static internalconfig.game.components.ComponentTypes.DeathCommandComponentType;

public class DeathHandlingSystem extends AbstractSingleInstanceSystem<Double> {

    private final AbstractComponentType<DeathCommands> deathCommandComponentType;

    private final DeathHandlers deathHandlers;

    public DeathHandlingSystem(AbstractComponentTypeContainer componentTypeContainer){
        deathCommandComponentType = componentTypeContainer.getTypeInstance(DeathCommandComponentType.class);
        deathHandlers = new DeathHandlers(componentTypeContainer);
    }

    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        List<Message<EntityHandle>> collisionList = sliceBoard.getMessageList(DEATHS);
        for(Message<EntityHandle> message : collisionList){
            handleDeath(ecsInterface, sliceBoard, message.getMessage());
        }

        sliceBoard.ageAndCullMessages();
    }

    private void handleDeath(AbstractECSInterface ecsInterface,
                             AbstractPublishSubscribeBoard sliceBoard,
                             EntityHandle deadEntity){

        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        if(dataStorage.containsComponent(deadEntity, deathCommandComponentType)){
            DeathCommands deathCommand = dataStorage.getComponent(deadEntity, deathCommandComponentType);
            deathHandlers.handleDeath(ecsInterface, deathCommand, deadEntity);
        }
        else {
            sliceBoard.publishMessage(ECSUtil.makeRemoveEntityMessage(new RemoveEntityOrder(deadEntity)));
        }
    }
}
