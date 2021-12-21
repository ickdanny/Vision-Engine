package internalconfig.game.systems.graphicssystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.component.componentstorage.AbstractGroup;
import ecs.component.componentstorage.ComponentIterator;
import ecs.datastorage.AbstractDataStorage;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.PlayerStateSystem;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.systems.Topics.PLAYER_STATE_ENTRY;

public class MakeOpaqueWhenPlayerFocusedAndAliveSystem implements AbstractSystem<Double> {

    public static final double OPACITY_CHANGE_PER_TICK = .03;

    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
    private final AbstractComponentType<Void> makeOpaqueWhenPlayerFocusedAndAliveMarker;

    public MakeOpaqueWhenPlayerFocusedAndAliveSystem(AbstractComponentTypeContainer componentTypeContainer){
        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(SpriteInstructionComponentType.class);
        makeOpaqueWhenPlayerFocusedAndAliveMarker = componentTypeContainer.getTypeInstance(MakeOpaqueWhenPlayerFocusedAndAliveMarker.class);
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double>{
        private AbstractGroup group;

        private PlayerStateSystem.States mostRecentPlayerState;

        private Instance() {
            group = null;
            mostRecentPlayerState = PlayerStateSystem.States.NORMAL;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {

            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            if (group == null) {
                getGroup(dataStorage);
            }

            updateMostRecentPlayerState(sliceBoard);

            boolean isPlayerFocusedAndAlive = isPlayerFocused(sliceBoard) && isPlayerAlive();

            ComponentIterator<SpriteInstruction> spriteItr = group.getComponentIterator(spriteInstructionComponentType);

            while(spriteItr.hasNext()){
                SpriteInstruction spriteInstruction = spriteItr.next();
                changeTransparency(spriteInstruction, isPlayerFocusedAndAlive);
            }

            //noinspection ConstantConditions
            if(spriteItr.hasNext()){
                throw new RuntimeException("unexpected extra component in iterator");
            }

            sliceBoard.ageAndCullMessages();
        }

        private void updateMostRecentPlayerState(AbstractPublishSubscribeBoard sliceBoard){
            if (sliceBoard.hasTopicalMessages(PLAYER_STATE_ENTRY)) {
                List<Message<PlayerStateSystem.States>> list = sliceBoard.getMessageList(PLAYER_STATE_ENTRY);
                for (Message<PlayerStateSystem.States> message : list) {
                    mostRecentPlayerState = message.getMessage();
                }
            }
        }

        private boolean isPlayerFocused(AbstractPublishSubscribeBoard sliceBoard){
            return GameUtil.isPlayerFocused(sliceBoard);
        }

        private boolean isPlayerAlive(){
            switch(mostRecentPlayerState){
                case NORMAL:
                case BOMBING:
                case RESPAWN_INVULNERABLE:
                    return true;
                case DEAD:
                case CHECK_CONTINUE:
                case RESPAWNING:
                case GAME_OVER:
                default:
                    return false;
            }
        }

        private void changeTransparency(SpriteInstruction spriteInstruction, boolean isPlayerFocusedAndAlive){
            double originalTransparency = spriteInstruction.getTransparency();
            if(isPlayerFocusedAndAlive && originalTransparency < 1d){
                spriteInstruction.setTransparency(Math.min(1d, originalTransparency + OPACITY_CHANGE_PER_TICK));
            }
            else if(!isPlayerFocusedAndAlive && originalTransparency > 0){
                spriteInstruction.setTransparency(Math.max(0d, originalTransparency - OPACITY_CHANGE_PER_TICK));
            }
        }

        private void getGroup(AbstractDataStorage dataStorage){
            group = dataStorage.createGroup(spriteInstructionComponentType, makeOpaqueWhenPlayerFocusedAndAliveMarker);
        }
    }
}