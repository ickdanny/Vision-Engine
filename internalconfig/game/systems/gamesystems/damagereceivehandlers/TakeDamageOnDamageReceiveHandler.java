package internalconfig.game.systems.gamesystems.damagereceivehandlers;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DamageReceiveCommands;
import internalconfig.game.components.HealthComponent;
import util.messaging.Message;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.components.DamageReceiveCommands.TAKE_DAMAGE;
import static internalconfig.game.systems.Topics.*;

class TakeDamageOnDamageReceiveHandler implements AbstractDamageReceiveHandler {

    private final AbstractComponentType<HealthComponent> healthComponentType;
    private final AbstractComponentType<Integer> damageComponentType;

    TakeDamageOnDamageReceiveHandler(AbstractComponentTypeContainer componentTypeContainer){
        healthComponentType = componentTypeContainer.getTypeInstance(HealthComponentType.class);
        damageComponentType = componentTypeContainer.getTypeInstance(DamageComponentType.class);
    }

    @Override
    public DamageReceiveCommands getCommand() {
        return TAKE_DAMAGE;
    }

    //if the new health is above zero, it will go through a setComponentOrder
    //otherwise, it will instead push a death message
    @Override
    public void handleDamageReceive(AbstractECSInterface ecsInterface, EntityHandle giver, EntityHandle receiver) {
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        if(dataStorage.containsComponent(receiver, healthComponentType)
                && dataStorage.containsComponent(giver, damageComponentType)){
            HealthComponent healthComponent = dataStorage.getComponent(receiver, healthComponentType);
            int health = healthComponent.getHealth();
            if(health > 0) {
                int damage = dataStorage.getComponent(giver, damageComponentType);

                int newHealth = health - damage;
                healthComponent.setHealth(newHealth);

                if (newHealth <= 0) {
                    int messageLifetime = dataStorage.getMessageLifetime();
                    ecsInterface.getSliceBoard().publishMessage(new Message<>(DEATHS, receiver, messageLifetime));
                }
            }
        }
    }
}