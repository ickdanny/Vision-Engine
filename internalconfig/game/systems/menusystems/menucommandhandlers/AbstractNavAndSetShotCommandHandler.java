package internalconfig.game.systems.menusystems.menucommandhandlers;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.ShotType;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.game.systems.Topics.*;

abstract class AbstractNavAndSetShotCommandHandler extends AbstractNavCommandHandler {

    private final ShotType shotType;

    AbstractNavAndSetShotCommandHandler(AbstractComponentTypeContainer componentTypeContainer,
                                        Class<? extends AbstractComponentType<EntityHandle>> directionComponentType,
                                        ShotType shotType) {
        super(componentTypeContainer, directionComponentType);
        this.shotType = shotType;
    }

    @Override
    public boolean handleCommand(AbstractECSInterface ecsInterface,
                                 AbstractMenuNavigationSystemInstance menuNavigationSystemInstance) {
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        sliceBoard.publishMessage(new Message<>(SHOT_TYPE_SELECTION_TOPIC, shotType, dataStorage.getMessageLifetime()));
        return super.handleCommand(ecsInterface, menuNavigationSystemInstance);
    }
}
