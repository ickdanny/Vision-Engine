package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import resource.Resource;

import java.util.Properties;

public class InstructionHandlers {
    private final AbstractInstructionHandler<?, ?>[] handlers;

    public InstructionHandlers(AbstractComponentTypeContainer componentTypeContainer,
                               Resource<Properties> propertiesResource){
        handlers = new AbstractInstructionHandler[Instructions.values().length];
        makeHandlers(componentTypeContainer, propertiesResource);
        throwIfHandlersIncludesNull();
    }

    private void makeHandlers(AbstractComponentTypeContainer componentTypeContainer,
                              Resource<Properties> propertiesResource){
        for(AbstractInstructionHandler<?, ?> handler : new AbstractInstructionHandler[]{
                new TimerInstructionHandler(),

                new RemoveVisibleInstructionHandler(componentTypeContainer),
                new ShiftTransparencyOverPeriodInstructionHandler(componentTypeContainer),
                new ShiftScaleOverPeriodInstructionHandler(componentTypeContainer),

                new SetSpriteInstructionInstructionHandler(componentTypeContainer),
                new SetAnimationInstructionHandler(componentTypeContainer),
                new SetDrawOrderInstructionHandler(componentTypeContainer),

                new WaitUntilSpawnComponentEmptyInstructionHandler(componentTypeContainer),
                new WaitUntilPlayerFocusedInstructionHandler(),
                new WaitUntilPlayerUnfocusedInstructionHandler(),
                new WaitUntilPlayerNormalInstructionHandler(),
                new WaitUntilPlayerBombingInstructionHandler(),
                new WaitUntilPlayerDeadInstructionHandler(),
                new WaitUntilPlayerRespawningInstructionHandler(),
                new WaitUntilPlayerRespawnInvulnerableInstructionHandler(),
                new WaitUntilBossDeathInstructionHandler(),
                new WaitUntilDialogueOverInstructionHandler(),

                new BoundaryYLowInstructionHandler(componentTypeContainer),
                new BoundaryYHighInstructionHandler(componentTypeContainer),
                new BoundaryXLowInstructionHandler(componentTypeContainer),
                new BoundaryXHighInstructionHandler(componentTypeContainer),
                new BoundaryYInstructionHandler(componentTypeContainer),
                new BoundaryXInstructionHandler(componentTypeContainer),
                new BoundaryInstructionHandler(componentTypeContainer),

                new SetCollidableInstructionHandler(componentTypeContainer),
                new RemoveCollidableInstructionHandler(componentTypeContainer),

                new SetHealthInstructionHandler(componentTypeContainer),
                new RemoveHealthInstructionHandler(componentTypeContainer),

                new SetDamageInstructionHandler(componentTypeContainer),
                new RemoveDamageInstructionHandler(componentTypeContainer),

                new SetSpawnInstructionHandler(componentTypeContainer),
                new AddSpawnInstructionHandler(componentTypeContainer),
                new ClearSpawnInstructionHandler(componentTypeContainer),

                new SetVelocityInstructionHandler(componentTypeContainer),
                new SetVelocityToPlayerInstructionHandler(componentTypeContainer),
                new SetRandomVelocityInstructionHandler(componentTypeContainer),
                new RemoveVelocityInstructionHandler(componentTypeContainer),

                new SetInboundInstructionHandler(componentTypeContainer),
                new RemoveInboundInstructionHandler(componentTypeContainer),

                new SetOutboundInstructionHandler(componentTypeContainer),

                new SlowToHaltInstructionHandler(componentTypeContainer),
                new SpeedUpToVelocityInstructionHandler(componentTypeContainer),
                new SpeedUpAndTurnToVelocityInstructionHandler(componentTypeContainer),
                new SpeedUpAndTurnToVelocityLongAngleInstructionHandler(componentTypeContainer),
                new SlowDownToVelocityInstructionHandler(componentTypeContainer),
                new SlowDownAndTurnToVelocityInstructionHandler(componentTypeContainer),
                new SlowDownAndTurnToVelocityLongAngleInstructionHandler(componentTypeContainer),

                new TurnToInstructionHandler(componentTypeContainer),
                new TurnToLongAngleInstructionHandler(componentTypeContainer),

                new AccelerateToSpeedInstructionHandler(componentTypeContainer),
                new DecelerateToSpeedInstructionHandler(componentTypeContainer),

                new GotoDeceleratingInstructionHandler(componentTypeContainer),
                new BoundRadiusGotoDeceleratingInstructionHandler(componentTypeContainer),

                new FollowPlayerInstructionHandler(componentTypeContainer),

                new ClearFieldInstructionHandler(componentTypeContainer),
                new ClearFieldLongInstructionHandler(componentTypeContainer),

                new DieInstructionHandler(),
                new RemoveEntityInstructionHandler(),

                new StartTrackInstructionHandler(),

                new ShowDialogueInstructionHandler(),
                new NextStageInstructionHandler(),
                new GameOverInstructionHandler(),
                new GameWinInstructionHandler(propertiesResource),
        }){
            handlers[handler.getInstruction().getIndex()] = handler;
        }
    }

    private void throwIfHandlersIncludesNull(){
        for(Instructions<?, ?> instruction : Instructions.values()){
            if(handlers[instruction.getIndex()] == null){
                throw new RuntimeException("handler for " + instruction + " is null");
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T, V> boolean handleInstruction(AbstractECSInterface ecsInterface,
                                  InstructionNode<T, V> instructionNode,
                                  InstructionDataMap dataMap,
                                  int entityID){

        Instructions<T, V> instruction = instructionNode.getInstruction();
        AbstractInstructionHandler<T, V> handler = (AbstractInstructionHandler<T, V>)handlers[instruction.getIndex()];
        if(handler == null){
            throw new RuntimeException("cannot find handler for " + instruction);
        }
        return handler.handleInstruction(ecsInterface, instructionNode, dataMap, entityID);
    }
}
