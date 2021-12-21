package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.systems.PlayerData;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static ecs.ECSTopics.PUSH_NEW_SLICE;
import static internalconfig.game.systems.Topics.*;
import static internalconfig.game.GlobalTopics.*;
import static internalconfig.game.systems.SliceCodes.CONTINUE;

@SuppressWarnings("unused")
public class ContinueAndGameOverSystem implements AbstractSystem<Double> {

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private static class Instance implements AbstractSystemInstance<Double> {

        private PlayerData playerData;
        private boolean primedToReceiveContinueScreenResults;

        private Instance() {
            playerData = null;
            primedToReceiveContinueScreenResults = false;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            if (playerData == null) {
                playerData = GameUtil.getPlayerData(sliceBoard);
            }
            if (primedToReceiveContinueScreenResults) {
                receiveContinueScreenResults(ecsInterface, sliceBoard);
                primedToReceiveContinueScreenResults = false;
            } else if (hasDiedWithNoLives(sliceBoard)) {
                continueOrGameOver(ecsInterface, sliceBoard);
            }

            sliceBoard.ageAndCullMessages();
        }

        private void receiveContinueScreenResults(AbstractECSInterface ecsInterface,
                                                  AbstractPublishSubscribeBoard sliceBoard) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            if (globalBoard.hasTopicalMessages(CONTINUE_SCREEN_GAME_OVER)) {
                globalBoard.getMessageList(CONTINUE_SCREEN_GAME_OVER).clear();
                broadcastGameOver(ecsInterface, sliceBoard, 1);
            }
        }

        private void broadcastGameOver(AbstractECSInterface ecsInterface,
                                       AbstractPublishSubscribeBoard sliceBoard,
                                       int ticks) {
            int messageLifetimeMultiplier = ecsInterface.getSliceData().getMessageLifetime();
            int messageLifetime = ticks * messageLifetimeMultiplier;
            sliceBoard.publishMessage(new Message<>(GAME_OVER, null, messageLifetime));
        }

        private boolean hasDiedWithNoLives(AbstractPublishSubscribeBoard sliceBoard) {
            if (sliceBoard.hasTopicalMessages(PLAYER_STATE_ENTRY)) {
                List<Message<PlayerStateSystem.States>> list = sliceBoard.getMessageList(PLAYER_STATE_ENTRY);
                for (Message<PlayerStateSystem.States> message : list) {
                    if (message.getMessage() == PlayerStateSystem.States.CHECK_CONTINUE) {
                        return playerData.getLives() <= 0;
                    }
                }
            }
            return false;
        }

        private void continueOrGameOver(AbstractECSInterface ecsInterface, AbstractPublishSubscribeBoard sliceBoard) {
            int continues = playerData.getContinues();
            if (continues > 0) {
                int messageLifetime = ecsInterface.getSliceData().getMessageLifetime();
                sliceBoard.publishMessage(new Message<>(CONTINUE_USED, null, messageLifetime * 2));
                enterContinueScreen(ecsInterface, sliceBoard);
                primedToReceiveContinueScreenResults = true;
            } else {
                broadcastGameOver(ecsInterface, sliceBoard, 35);
            }
        }

        private void enterContinueScreen(AbstractECSInterface ecsInterface, AbstractPublishSubscribeBoard sliceBoard) {
            int continues = GameUtil.getPlayerData(sliceBoard).getContinues();
            String sliceCode = CONTINUE + continues;
            sliceBoard.publishMessage(new Message<>(PUSH_NEW_SLICE, sliceCode, Message.AGELESS));
            ecsInterface.getGlobalBoard().publishMessage(
                    new Message<>(TOP_LEVEL_SLICES, sliceCode, Message.AGELESS));
            sliceBoard.publishMessage(new Message<>(PAUSE_STATE, null, Message.AGELESS));
        }
    }
}
