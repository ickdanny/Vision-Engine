package internalconfig.game.systems.gamesystems.deathhandlers;

import ecs.AbstractECSInterface;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DeathCommands;

import java.util.EnumMap;

public class DeathHandlers {
    private final EnumMap<DeathCommands, AbstractDeathHandler> handlers;

    public DeathHandlers(AbstractComponentTypeContainer componentTypeContainer){
        handlers = new EnumMap<>(DeathCommands.class);
        makeHandlers(componentTypeContainer);
        throwIfHandlersIncludesNull();
    }

    private void makeHandlers(AbstractComponentTypeContainer componentTypeContainer){
        for(AbstractDeathHandler handler : new AbstractDeathHandler[]{
                new PlayerDeathHandler(componentTypeContainer),
                new BossDeathHandler(),
                new PickupDeathHandler(componentTypeContainer),
                new DeathSpawnHandler(componentTypeContainer),
        }){
            handlers.put(handler.getCommand(), handler);
        }
    }

    private void throwIfHandlersIncludesNull(){
        for(DeathCommands damageReceiveCommand : DeathCommands.values()){
            if(handlers.get(damageReceiveCommand) == null){
                throw new RuntimeException("handler for " + damageReceiveCommand + " is null");
            }
        }
    }

    public void handleDeath(AbstractECSInterface ecsInterface,
                                    DeathCommands damageReceiveCommand,
                                    EntityHandle deadEntity){

        AbstractDeathHandler handler = handlers.get(damageReceiveCommand);
        if(handler == null){
            throw new RuntimeException("cannot find handler for " + damageReceiveCommand);
        }
        handler.handleDeath(ecsInterface, deadEntity);
    }
}
