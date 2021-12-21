package internalconfig.game.systems.gamesystems.damagegivehandlers;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.criticalorders.RemoveComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DamageGiveCommands;
import internalconfig.game.components.VelocityComponent;
import internalconfig.game.systems.ShotType;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.GameConfig.*;

class BulletSlowOnDamageGiveHandler implements AbstractDamageGiveHandler {

    private final AbstractComponentType<VelocityComponent> velocityComponentType;
    private final AbstractComponentType<Void> bulletSlowDamageReceive;
    private final ShotType shotType;

    public BulletSlowOnDamageGiveHandler(AbstractComponentTypeContainer componentTypeContainer, ShotType shotType) {
        velocityComponentType = componentTypeContainer.getTypeInstance(VelocityComponentType.class);
        bulletSlowDamageReceive = componentTypeContainer.getTypeInstance(BulletSlowDamage.Receive.class);
        this.shotType = shotType;
    }

    @Override
    public DamageGiveCommands getCommand() {
        return DamageGiveCommands.BULLET_SLOW;
    }

    @Override
    public void handleDamageGive(AbstractECSInterface ecsInterface, EntityHandle giver, EntityHandle receiver) {
        switch(shotType){
            case A:
                break;
            case B:
                slowBullet(ecsInterface, receiver, BULLET_SLOW_RATE_SMALL, BULLET_SLOW_MAX_SLOW_SMALL);
                break;
            case C:
                slowBullet(ecsInterface, receiver, BULLET_SLOW_RATE_LARGE, BULLET_SLOW_MAX_SLOW_LARGE);
                break;
        }
    }

    private void slowBullet(AbstractECSInterface ecsInterface, EntityHandle bullet, double slowRate, double maxSlow){
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        if(dataStorage.containsComponent(bullet, velocityComponentType)){
            VelocityComponent velocityComponent = dataStorage.getComponent(bullet, velocityComponentType);
            double currentMulti = velocityComponent.getSpeedMulti();
            if(currentMulti > maxSlow){
                double nextMulti = currentMulti - slowRate;
                if(nextMulti > maxSlow){
                    velocityComponent.setSpeedMulti(nextMulti);
                }
                else{
                    velocityComponent.setSpeedMulti(maxSlow);
                    removeReceiveMarker(ecsInterface, bullet);
                }
            }
        }
    }

    private void removeReceiveMarker(AbstractECSInterface ecsInterface, EntityHandle bullet){
        ecsInterface.getSliceBoard().publishMessage(
                ECSUtil.makeRemoveComponentMessage(new RemoveComponentOrder(bullet, bulletSlowDamageReceive))
        );
    }
}
