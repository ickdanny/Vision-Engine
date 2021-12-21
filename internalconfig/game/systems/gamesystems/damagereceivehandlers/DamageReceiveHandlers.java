package internalconfig.game.systems.gamesystems.damagereceivehandlers;

import ecs.AbstractECSInterface;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DamageReceiveCommands;

import java.util.EnumMap;

@SuppressWarnings("unused")
public class DamageReceiveHandlers {
    private final EnumMap<DamageReceiveCommands, AbstractDamageReceiveHandler> handlers;

    public DamageReceiveHandlers(AbstractComponentTypeContainer componentTypeContainer){
        handlers = new EnumMap<>(DamageReceiveCommands.class);
        makeHandlers(componentTypeContainer);
        throwIfHandlersIncludesNull();
    }

    private void makeHandlers(AbstractComponentTypeContainer componentTypeContainer){
        for(AbstractDamageReceiveHandler handler : new AbstractDamageReceiveHandler[]{
                new DeathOnDamageReceiveHandler(),
                new TakeDamageOnDamageReceiveHandler(componentTypeContainer),
                new PlayerDamageReceiveHandler(),
        }){
            handlers.put(handler.getCommand(), handler);
        }

    }

    private void throwIfHandlersIncludesNull(){
        for(DamageReceiveCommands damageReceiveCommand : DamageReceiveCommands.values()){
            if(handlers.get(damageReceiveCommand) == null){
                throw new RuntimeException("handler for " + damageReceiveCommand + " is null");
            }
        }
    }

    public void handleDamageReceive(AbstractECSInterface ecsInterface,
                                    DamageReceiveCommands damageReceiveCommand,
                                    EntityHandle giver,
                                    EntityHandle receiver){

        AbstractDamageReceiveHandler handler = handlers.get(damageReceiveCommand);
        if(handler == null){
            throw new RuntimeException("cannot find handler for " + damageReceiveCommand);
        }
        handler.handleDamageReceive(ecsInterface, giver, receiver);
    }
}
