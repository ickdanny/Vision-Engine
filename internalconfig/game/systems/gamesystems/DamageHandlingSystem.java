package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DamageGiveCommands;
import internalconfig.game.components.DamageReceiveCommands;
import internalconfig.game.systems.gamesystems.damagegivehandlers.DamageGiveHandlers;
import internalconfig.game.systems.gamesystems.damagereceivehandlers.DamageReceiveHandlers;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;
import util.tuple.Tuple2;

import java.util.List;

import static internalconfig.game.systems.Topics.*;
import static internalconfig.game.components.ComponentTypes.*;

@SuppressWarnings("unused")
public class DamageHandlingSystem implements AbstractSystem<Double> {

    private final AbstractComponentTypeContainer componentTypeContainer;

    private final AbstractComponentType<DamageGiveCommands> damageGiveCommandComponentType;
    private final AbstractComponentType<DamageReceiveCommands> damageReceiveCommandComponentType;

    public DamageHandlingSystem(AbstractComponentTypeContainer componentTypeContainer){
        this.componentTypeContainer = componentTypeContainer;

        damageGiveCommandComponentType = componentTypeContainer.getTypeInstance(DamageGiveCommandComponentType.class);
        damageReceiveCommandComponentType = componentTypeContainer.getTypeInstance(
                DamageReceiveCommandComponentType.class
        );
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double> {
        private DamageGiveHandlers damageGiveHandlers;
        private final DamageReceiveHandlers damageReceiveHandlers;

        private Instance(){
            damageGiveHandlers = null;
            damageReceiveHandlers = new DamageReceiveHandlers(componentTypeContainer);
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            if (damageGiveHandlers == null) {
                makeDamageGiveHandlers(componentTypeContainer, ecsInterface);
            }

            List<Message<Tuple2<EntityHandle, EntityHandle>>> collisionList = sliceBoard.getMessageList(DAMAGES);
            for (Message<Tuple2<EntityHandle, EntityHandle>> message : collisionList) {
                handleDamage(ecsInterface, message.getMessage());
            }

            sliceBoard.ageAndCullMessages();
        }

        private void handleDamage(AbstractECSInterface ecsInterface, Tuple2<EntityHandle, EntityHandle> entityTuple) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            EntityHandle giver = entityTuple.a;
            EntityHandle receiver = entityTuple.b;

            if (dataStorage.containsComponent(giver, damageGiveCommandComponentType)) {
                DamageGiveCommands damageGiveCommand = dataStorage.getComponent(giver, damageGiveCommandComponentType);
                damageGiveHandlers.handleDamageGive(ecsInterface, damageGiveCommand, giver, receiver);
            }
            if (dataStorage.containsComponent(receiver, damageReceiveCommandComponentType)) {
                DamageReceiveCommands damageReceiveCommand = dataStorage.getComponent(
                        receiver, damageReceiveCommandComponentType
                );
                damageReceiveHandlers.handleDamageReceive(ecsInterface, damageReceiveCommand, giver, receiver);
            }
        }

        private void makeDamageGiveHandlers(AbstractComponentTypeContainer componentTypeContainer,
                                            AbstractECSInterface ecsInterface) {
            damageGiveHandlers = new DamageGiveHandlers(componentTypeContainer,
                    GameUtil.getGameConfigObject(ecsInterface.getGlobalBoard()));
        }
    }
}
