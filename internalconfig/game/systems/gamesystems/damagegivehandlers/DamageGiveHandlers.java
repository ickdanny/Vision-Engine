package internalconfig.game.systems.gamesystems.damagegivehandlers;

import ecs.AbstractECSInterface;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;

import internalconfig.game.components.DamageGiveCommands;
import internalconfig.game.systems.GameConfigObject;
import internalconfig.game.systems.PlayerData;

import java.util.EnumMap;

public class DamageGiveHandlers {

    private final EnumMap<DamageGiveCommands, AbstractDamageGiveHandler> handlers;

    public DamageGiveHandlers(AbstractComponentTypeContainer componentTypeContainer, GameConfigObject gameConfigObject){
        handlers = new EnumMap<>(DamageGiveCommands.class);
        makeHandlers(componentTypeContainer, gameConfigObject);
        throwIfHandlersIncludesNull();
    }

    private void makeHandlers(AbstractComponentTypeContainer componentTypeContainer, GameConfigObject gameConfigObject){
        PlayerData playerData = gameConfigObject.getPlayerData();
        for(AbstractDamageGiveHandler handler : new AbstractDamageGiveHandler[]{
                new DeathOnDamageGiveHandler(),
                new BulletSlowOnDamageGiveHandler(componentTypeContainer, playerData.getShotType()),
        }){
            handlers.put(handler.getCommand(), handler);
        }
    }
    private void throwIfHandlersIncludesNull(){
        for(DamageGiveCommands damageGiveCommand : DamageGiveCommands.values()){
            if(handlers.get(damageGiveCommand) == null){
                throw new RuntimeException("handler for " + damageGiveCommand + " is null");
            }
        }
    }

    public void handleDamageGive(AbstractECSInterface ecsInterface,
                                 DamageGiveCommands damageGiveCommand,
                                 EntityHandle giver,
                                 EntityHandle receiver){

        AbstractDamageGiveHandler handler = handlers.get(damageGiveCommand);
        if(handler == null){
            throw new RuntimeException("cannot find handler for " + damageGiveCommand);
        }
        handler.handleDamageGive(ecsInterface, giver, receiver);
    }
}
