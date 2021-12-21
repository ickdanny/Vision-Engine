package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.systems.PlayerData;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.game.systems.Topics.*;
import static internalconfig.game.GameConfig.*;

public class PlayerStateSystem implements AbstractSystem<Double> {

    private static final States ENTRY_STATE = States.NORMAL;

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance(); //Instance has state
    }

    private static class Instance implements AbstractSystemInstance<Double>, PlayerStateMachine{

        PlayerData playerData;
        States currentState;
        int timer;

        public Instance(){
            playerData = null;
            currentState = null;
            timer = NO_TIMER;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            if(playerData == null){
                init(ecsInterface, sliceBoard);
            }

            currentState.run(ecsInterface, sliceBoard, this);

            sliceBoard.ageAndCullMessages();
        }

        private void init(AbstractECSInterface ecsInterface, AbstractPublishSubscribeBoard sliceBoard){
            playerData = GameUtil.getPlayerData(sliceBoard);
            initStateMachine(ecsInterface, sliceBoard);
        }

        private void initStateMachine(AbstractECSInterface ecsInterface, AbstractPublishSubscribeBoard sliceBoard){
            currentState = ENTRY_STATE;
            currentState.onEntry(ecsInterface, sliceBoard, this);
        }

        @Override
        public PlayerData getPlayerData() {
            return playerData;
        }

        @Override
        public int getTimer() {
            return timer;
        }

        @Override
        public void setCurrentState(States currentState) {
            this.currentState = currentState;
        }

        @Override
        public void setTimer(int timer) {
            this.timer = timer;
        }
    }

    private interface PlayerStateMachine{
        int NO_TIMER = -2;

        PlayerData getPlayerData();
        int getTimer();
        void setCurrentState(States currentState);
        void setTimer(int timer);
    }

    public enum States{
        NORMAL(){
            @Override
            protected void onEntry(AbstractECSInterface ecsInterface,
                                   AbstractPublishSubscribeBoard sliceBoard,
                                   PlayerStateMachine playerStateMachine) {
                sliceBoard.publishMessage(States.makeEntryMessage(ecsInterface, this));
                playerStateMachine.setTimer(PlayerStateMachine.NO_TIMER);
            }

            @Override
            protected States runAndGetNextState(AbstractECSInterface ecsInterface,
                                                AbstractPublishSubscribeBoard sliceBoard,
                                                PlayerStateMachine playerStateMachine) {

                if(isPlayerBomb(sliceBoard, playerStateMachine)){
                    return BOMBING;
                }

                int timer = playerStateMachine.getTimer();
                if(timer > 0){
                    playerStateMachine.setTimer(timer - 1);
                }
                else if(timer == 0){
                    return DEAD;
                }
                else if(sliceBoard.hasTopicalMessages(PLAYER_HITS)){
                    playerStateMachine.setTimer(DEATH_BOMB_PERIOD);
                }
                return NORMAL;
            }

            private boolean isPlayerBomb(AbstractPublishSubscribeBoard sliceBoard,
                                         PlayerStateMachine playerStateMachine){

                PlayerData playerData = playerStateMachine.getPlayerData();
                if(playerData.getBombs() > 0) {
                    for (Message<GameCommands> gameCommandsMessage : sliceBoard.getMessageList(GAME_COMMANDS)) {
                        if (gameCommandsMessage.getMessage() == GameCommands.BOMB) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            protected void onExit(AbstractECSInterface ecsInterface,
                                  AbstractPublishSubscribeBoard sliceBoard,
                                  PlayerStateMachine playerStateMachine) {
                playerStateMachine.setTimer(PlayerStateMachine.NO_TIMER);
            }
        },
        BOMBING(){
            @Override
            protected void onEntry(AbstractECSInterface ecsInterface,
                                   AbstractPublishSubscribeBoard sliceBoard,
                                   PlayerStateMachine playerStateMachine) {
                sliceBoard.publishMessage(States.makeEntryMessage(ecsInterface, this));
                playerStateMachine.setTimer(BOMB_INVULNERABILITY_PERIOD);
            }

            @Override
            protected States runAndGetNextState(AbstractECSInterface ecsInterface,
                                                AbstractPublishSubscribeBoard sliceBoard,
                                                PlayerStateMachine playerStateMachine) {

                int timer = playerStateMachine.getTimer();
                if(timer > 0){
                    playerStateMachine.setTimer(timer - 1);
                }
                else if(timer == 0){
                    return NORMAL;
                }
                return BOMBING;
            }

            @Override
            protected void onExit(AbstractECSInterface ecsInterface,
                                  AbstractPublishSubscribeBoard sliceBoard,
                                  PlayerStateMachine playerStateMachine) {
                playerStateMachine.setTimer(PlayerStateMachine.NO_TIMER);
            }
        },
        DEAD(){
            @Override
            protected void onEntry(AbstractECSInterface ecsInterface,
                                   AbstractPublishSubscribeBoard sliceBoard,
                                   PlayerStateMachine playerStateMachine) {
                sliceBoard.publishMessage(States.makeEntryMessage(ecsInterface, this));
                playerStateMachine.setTimer(DEAD_PERIOD);
            }

            @Override
            protected States runAndGetNextState(AbstractECSInterface ecsInterface,
                                                AbstractPublishSubscribeBoard sliceBoard,
                                                PlayerStateMachine playerStateMachine) {

                int timer = playerStateMachine.getTimer();
                if(timer > 0){
                    playerStateMachine.setTimer(timer - 1);
                }
                else if(timer == 0){
                    return CHECK_CONTINUE;
                }
                return DEAD;
            }

            @Override
            protected void onExit(AbstractECSInterface ecsInterface,
                                  AbstractPublishSubscribeBoard sliceBoard,
                                  PlayerStateMachine playerStateMachine) {
                playerStateMachine.setTimer(playerStateMachine.NO_TIMER);
            }
        },
        CHECK_CONTINUE(){
            @Override
            protected void onEntry(AbstractECSInterface ecsInterface,
                                   AbstractPublishSubscribeBoard sliceBoard,
                                   PlayerStateMachine playerStateMachine) {

                sliceBoard.publishMessage(States.makeEntryMessage(ecsInterface, this));
                playerStateMachine.setTimer(0);
            }

            @Override
            protected States runAndGetNextState(AbstractECSInterface ecsInterface,
                                                AbstractPublishSubscribeBoard sliceBoard,
                                                PlayerStateMachine playerStateMachine) {
                int timer = playerStateMachine.getTimer();
                if(timer > 0){
                    playerStateMachine.setTimer(timer - 1);
                }
                else if(timer == 0){
                    PlayerData playerData = playerStateMachine.getPlayerData();
                    if(playerData.getLives() <= 0 && playerData.getContinues() <= 0){
                        return GAME_OVER;
                    }
                    return RESPAWNING;
                }
                return CHECK_CONTINUE;
            }

            @Override
            protected void onExit(AbstractECSInterface ecsInterface,
                                  AbstractPublishSubscribeBoard sliceBoard,
                                  PlayerStateMachine playerStateMachine) {
                playerStateMachine.setTimer(playerStateMachine.NO_TIMER);
            }
        },
        RESPAWNING(){
            @Override
            protected void onEntry(AbstractECSInterface ecsInterface,
                                   AbstractPublishSubscribeBoard sliceBoard,
                                   PlayerStateMachine playerStateMachine) {
                sliceBoard.publishMessage(States.makeEntryMessage(ecsInterface, this));
                playerStateMachine.setTimer(RESPAWN_PERIOD);
            }

            @Override
            protected States runAndGetNextState(AbstractECSInterface ecsInterface,
                                                AbstractPublishSubscribeBoard sliceBoard,
                                                PlayerStateMachine playerStateMachine) {

                int timer = playerStateMachine.getTimer();
                if(timer > 0){
                    playerStateMachine.setTimer(timer - 1);
                }
                else if(timer == 0){
                    return RESPAWN_INVULNERABLE;
                }
                return RESPAWNING;
            }

            @Override
            protected void onExit(AbstractECSInterface ecsInterface,
                                  AbstractPublishSubscribeBoard sliceBoard,
                                  PlayerStateMachine playerStateMachine) {
                playerStateMachine.setTimer(PlayerStateMachine.NO_TIMER);
            }
        },
        RESPAWN_INVULNERABLE(){
            @Override
            protected void onEntry(AbstractECSInterface ecsInterface,
                                   AbstractPublishSubscribeBoard sliceBoard,
                                   PlayerStateMachine playerStateMachine) {
                sliceBoard.publishMessage(States.makeEntryMessage(ecsInterface, this));
                playerStateMachine.setTimer(RESPAWN_INVULNERABILITY_PERIOD);
            }

            @Override
            protected States runAndGetNextState(AbstractECSInterface ecsInterface,
                                                AbstractPublishSubscribeBoard sliceBoard,
                                                PlayerStateMachine playerStateMachine) {

                int timer = playerStateMachine.getTimer();
                if(timer > 0){
                    playerStateMachine.setTimer(timer - 1);
                }
                else if(timer == 0){
                    return NORMAL;
                }
                return RESPAWN_INVULNERABLE;
            }

            @Override
            protected void onExit(AbstractECSInterface ecsInterface,
                                  AbstractPublishSubscribeBoard sliceBoard,
                                  PlayerStateMachine playerStateMachine) {
                playerStateMachine.setTimer(PlayerStateMachine.NO_TIMER);
            }
        },
        GAME_OVER(){
            @Override
            protected void onEntry(AbstractECSInterface ecsInterface, AbstractPublishSubscribeBoard sliceBoard, PlayerStateMachine playerStateMachine) {
                sliceBoard.publishMessage(States.makeEntryMessage(ecsInterface, this));
                playerStateMachine.setTimer(PlayerStateMachine.NO_TIMER);
            }

            @Override
            protected States runAndGetNextState(AbstractECSInterface ecsInterface, AbstractPublishSubscribeBoard sliceBoard, PlayerStateMachine playerStateMachine) {
                return GAME_OVER;
            }
        }
        ;

        private static Message<States> makeEntryMessage(AbstractECSInterface ecsInterface, States state){
            return new Message<>(PLAYER_STATE_ENTRY, state, ecsInterface.getSliceData().getMessageLifetime());
        }

        protected void onEntry(AbstractECSInterface ecsInterface,
                               AbstractPublishSubscribeBoard sliceBoard,
                               PlayerStateMachine playerStateMachine){}

        protected final void run(AbstractECSInterface ecsInterface,
                           AbstractPublishSubscribeBoard sliceBoard,
                           PlayerStateMachine playerStateMachine){

            States nextState = runAndGetNextState(ecsInterface, sliceBoard, playerStateMachine);
            if (nextState != this && nextState != null) {
                onExit(ecsInterface, sliceBoard, playerStateMachine);
                nextState.onEntry(ecsInterface, sliceBoard, playerStateMachine);
                playerStateMachine.setCurrentState(nextState);
            }
        }

        protected abstract States runAndGetNextState(AbstractECSInterface ecsInterface,
                                                     AbstractPublishSubscribeBoard sliceBoard,
                                                     PlayerStateMachine playerStateMachine);

        protected void onExit(AbstractECSInterface ecsInterface,
                              AbstractPublishSubscribeBoard sliceBoard,
                              PlayerStateMachine playerStateMachine){}
    }
}