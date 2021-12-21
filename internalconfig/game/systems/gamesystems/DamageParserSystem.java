package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSingleInstanceSystem;
import internalconfig.game.components.AbstractComponentTypeContainer;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;
import util.tuple.Tuple2;

import java.util.List;

import static internalconfig.game.systems.Topics.*;
import static internalconfig.game.components.ComponentTypes.*;

public class DamageParserSystem extends AbstractSingleInstanceSystem<Double> {

    private final AbstractComponentType<Void> playerDamageGive;
    private final AbstractComponentType<Void> playerDamageReceive;
    private final AbstractComponentType<Void> bombDamageGive;
    private final AbstractComponentType<Void> bombDamageReceive;
    private final AbstractComponentType<Void> enemyDamageGive;
    private final AbstractComponentType<Void> enemyDamageReceive;
    private final AbstractComponentType<Void> pickupDamageGive;
    private final AbstractComponentType<Void> pickupDamageReceive;
    private final AbstractComponentType<Void> bulletSlowDamageGive;
    private final AbstractComponentType<Void> bulletSlowDamageReceive;

    public DamageParserSystem(AbstractComponentTypeContainer componentTypeContainer){
        playerDamageGive = componentTypeContainer.getTypeInstance(PlayerDamage.Give.class);
        playerDamageReceive = componentTypeContainer.getTypeInstance(PlayerDamage.Receive.class);
        bombDamageGive = componentTypeContainer.getTypeInstance(BombDamage.Give.class);
        bombDamageReceive = componentTypeContainer.getTypeInstance(BombDamage.Receive.class);
        enemyDamageGive = componentTypeContainer.getTypeInstance(EnemyDamage.Give.class);
        enemyDamageReceive = componentTypeContainer.getTypeInstance(EnemyDamage.Receive.class);
        pickupDamageGive = componentTypeContainer.getTypeInstance(PickupDamage.Give.class);
        pickupDamageReceive = componentTypeContainer.getTypeInstance(PickupDamage.Receive.class);
        bulletSlowDamageGive = componentTypeContainer.getTypeInstance(BulletSlowDamage.Give.class);
        bulletSlowDamageReceive = componentTypeContainer.getTypeInstance(BulletSlowDamage.Receive.class);
    }

    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        List<Message<Tuple2<EntityHandle, EntityHandle>>> collisionList = sliceBoard.getMessageList(COLLISIONS);
        for(Message<Tuple2<EntityHandle, EntityHandle>> message : collisionList){
            checkForDamage(dataStorage, sliceBoard, message.getMessage());
        }

        sliceBoard.ageAndCullMessages();
    }

    private void checkForDamage(AbstractDataStorage dataStorage,
                                AbstractPublishSubscribeBoard sliceBoard,
                                Tuple2<EntityHandle, EntityHandle> entityTuple){

        checkForDamageWithTypes(dataStorage, sliceBoard, entityTuple, playerDamageGive, playerDamageReceive);
        checkForDamageWithTypes(dataStorage, sliceBoard, entityTuple, bombDamageGive, bombDamageReceive);
        checkForDamageWithTypes(dataStorage, sliceBoard, entityTuple, enemyDamageGive, enemyDamageReceive);
        checkForDamageWithTypes(dataStorage, sliceBoard, entityTuple, pickupDamageGive, pickupDamageReceive);
        checkForDamageWithTypes(dataStorage, sliceBoard, entityTuple, bulletSlowDamageGive, bulletSlowDamageReceive);
    }

    private void checkForDamageWithTypes(AbstractDataStorage dataStorage,
                                         AbstractPublishSubscribeBoard sliceBoard,
                                         Tuple2<EntityHandle, EntityHandle> entityTuple,
                                         AbstractComponentType<?> giveType,
                                         AbstractComponentType<?> receiveType){

        EntityHandle entityA = entityTuple.a;
        EntityHandle entityB = entityTuple.b;
        int messageLifetime = dataStorage.getMessageLifetime();

        if(dataStorage.containsComponent(entityA, giveType) && dataStorage.containsComponent(entityB, receiveType)){
            sliceBoard.publishMessage(new Message<>(DAMAGES, entityTuple, messageLifetime));
        }
        //flip order - need to make new tuple
        if(dataStorage.containsComponent(entityB, giveType) && dataStorage.containsComponent(entityA, receiveType)){
            sliceBoard.publishMessage(new Message<>(DAMAGES, new Tuple2<>(entityB, entityA), messageLifetime));
        }
    }
}
