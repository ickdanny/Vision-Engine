package internalconfig.game.systems.dialoguesystems;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import ecs.system.criticalorders.SetComponentOrder;
import internalconfig.game.SliceUtil;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.components.TextSpriteInstruction;
import resource.AbstractResourceManager;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;
import util.messaging.Topic;

import java.util.List;
import java.util.NoSuchElementException;

import static internalconfig.game.GlobalTopics.DIALOGUE_CODE;
import static internalconfig.game.GlobalTopics.DIALOGUE_OVER;
import static internalconfig.game.systems.Topics.LOWER_IMAGE_HANDLE;
import static internalconfig.game.systems.Topics.LOWER_TEXT_HANDLE;
import static internalconfig.game.systems.Topics.MUSIC;
import static internalconfig.game.systems.Topics.READ_DIALOGUE_COMMAND;
import static internalconfig.game.systems.Topics.UPPER_IMAGE_HANDLE;
import static internalconfig.game.systems.Topics.UPPER_TEXT_HANDLE;

import static internalconfig.game.components.ComponentTypes.*;

@SuppressWarnings("unused")
public class DialogueSystem implements AbstractSystem<Double> {

    private final AbstractResourceManager<Dialogue> dialogueManager;

    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;

    public DialogueSystem(AbstractResourceManager<Dialogue> dialogueManager,
                          AbstractComponentTypeContainer componentTypeContainer){
        this.dialogueManager = dialogueManager;
        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(SpriteInstructionComponentType.class);
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double> {

        private static final int CHARS_PER_LINE = 31;

        private static final int COOLDOWN = 2;

        private Dialogue dialogue;

        private EntityHandle lowerImageHandle;
        private EntityHandle upperImageHandle;
        private EntityHandle lowerTextHandle;
        private EntityHandle upperTextHandle;

        private int currentCoolDown;

        private int currentPos;

        private Instance() {
            dialogue = null;
            lowerImageHandle = null;
            upperImageHandle = null;
            lowerTextHandle = null;
            upperTextHandle = null;
            currentCoolDown = 0;
            currentPos = 0;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            if (dialogue == null) {
                init(ecsInterface);
            }

            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            if (currentCoolDown > 0) {
                --currentCoolDown;
            } else {
                if(sliceBoard.hasTopicalMessages(READ_DIALOGUE_COMMAND)){
                    readDialogue(ecsInterface);
                    currentCoolDown = COOLDOWN;
                }
            }
            sliceBoard.ageAndCullMessages();
        }

        private void readDialogue(AbstractECSInterface ecsInterface){
            while(currentPos < dialogue.size()){
                if(executeCommand(ecsInterface, dialogue.getCommandDataTuple(currentPos))){
                    ++currentPos;
                }
                else{
                    ++currentPos;
                    break;
                }
            }
            if (currentPos >= dialogue.size()) {
                exitDialogue(ecsInterface);
            }
        }

        //true if continue, false if block
        private boolean executeCommand(AbstractECSInterface ecsInterface, CommandDataTuple commandDataTuple){
            switch(commandDataTuple.getCommand()){
                case SET_LOWER_IMAGE:
                    setImage(ecsInterface, lowerImageHandle, commandDataTuple.getData());
                    return true;
                case SET_UPPER_IMAGE:
                    setImage(ecsInterface, upperImageHandle, commandDataTuple.getData());
                    return true;
                case SET_LOWER_TEXT:
                    setText(ecsInterface, lowerTextHandle, commandDataTuple.getData());
                    return true;
                case SET_UPPER_TEXT:
                    setText(ecsInterface, upperTextHandle, commandDataTuple.getData());
                    return true;
                case START_TRACK:
                    startTrack(ecsInterface, commandDataTuple.getData());
                    return true;
                case STOP:
                    return false;
                default:
                    throw new RuntimeException("unaccounted command: " + commandDataTuple.getCommand());
            }
        }

        private void setImage(AbstractECSInterface ecsInterface, EntityHandle entityHandle, String image){
            SpriteInstruction spriteInstruction = new SpriteInstruction(image);
            ecsInterface.getSliceBoard().publishMessage(
                    ECSUtil.makeSetComponentMessage(
                            new SetComponentOrder<>(entityHandle, spriteInstructionComponentType, spriteInstruction)
                    )
            );
        }

        private void setText(AbstractECSInterface ecsInterface, EntityHandle entityHandle, String text){
            TextSpriteInstruction textSpriteInstruction = new TextSpriteInstruction(text, CHARS_PER_LINE);
            ecsInterface.getSliceBoard().publishMessage(
                    ECSUtil.makeSetComponentMessage(
                            new SetComponentOrder<>(entityHandle, spriteInstructionComponentType, textSpriteInstruction)
                    )
            );
        }

        private void startTrack(AbstractECSInterface ecsInterface, String trackID){
            ecsInterface.getSliceBoard().publishMessage(new Message<>(MUSIC, trackID, Message.AGELESS));
        }

        private void exitDialogue(AbstractECSInterface ecsInterface){
            ecsInterface.getGlobalBoard().publishMessage(new Message<>(DIALOGUE_OVER, null, Message.AGELESS));
            SliceUtil.back(ecsInterface);
        }

        private void init(AbstractECSInterface ecsInterface) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            lowerImageHandle = getHandleInTopic(LOWER_IMAGE_HANDLE, sliceBoard);
            upperImageHandle = getHandleInTopic(UPPER_IMAGE_HANDLE, sliceBoard);
            lowerTextHandle = getHandleInTopic(LOWER_TEXT_HANDLE, sliceBoard);
            upperTextHandle = getHandleInTopic(UPPER_TEXT_HANDLE, sliceBoard);
            dialogue = getDialogue(ecsInterface);
        }

        private EntityHandle getHandleInTopic(Topic<EntityHandle> topic, AbstractPublishSubscribeBoard sliceBoard) {
            try {
                return sliceBoard.getMessageList(topic).iterator().next().getMessage();
            } catch (NoSuchElementException nsee) {
                throw new RuntimeException("cannot find handle in topic: " + topic, nsee);
            }
        }

        private Dialogue getDialogue(AbstractECSInterface ecsInterface){
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            if(globalBoard.hasTopicalMessages(DIALOGUE_CODE)){
                List<Message<String>> messageList = globalBoard.getMessageList(DIALOGUE_CODE);
                if(messageList.size() > 1){
                    throw new RuntimeException("more than 1 dialogue code!");
                }
                Message<String> message = messageList.iterator().next();
                String code = message.getMessage();
                Dialogue dialogue = dialogueManager.getResource(code).getData();
                messageList.clear();
                return dialogue;
            } else {
                throw new RuntimeException("unable to find dialogue code");
            }
        }
    }
}
