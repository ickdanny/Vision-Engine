package internalconfig.game.systems.soundsystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSingleInstanceSystem;
import resource.AbstractResourceManager;
import sound.midi.MidiSequence;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;
import util.observer.AbstractSubject;
import util.observer.ConfigurablePushSubject;

import java.util.Iterator;
import java.util.List;

import static internalconfig.game.systems.Topics.*;

public class MusicSystem extends AbstractSingleInstanceSystem<Double> {

    public static final String RESET_CODE = "reset";

    private final AbstractResourceManager<MidiSequence> midiSequenceManager;
    private final ConfigurablePushSubject<MidiSequence> trackStartBroadcaster;
    private final AbstractSubject sequencerResetBroadcaster;

    public MusicSystem(AbstractResourceManager<MidiSequence> midiSequenceManager,
                       ConfigurablePushSubject<MidiSequence> trackStartBroadcaster,
                       AbstractSubject sequencerResetBroadcaster){
        this.midiSequenceManager = midiSequenceManager;
        this.trackStartBroadcaster = trackStartBroadcaster;
        this.sequencerResetBroadcaster = sequencerResetBroadcaster;
    }

    @Override
    public void run(AbstractECSInterface ecsInterface, Double data) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        List<Message<String>> messageList = sliceBoard.getMessageList(MUSIC);
        if(messageList.size() > 1){
            throw new RuntimeException("too many start track messages! " + messageList);
        }
        if(messageList.size() == 1) {
            Iterator<Message<String>> messageItr = messageList.iterator();
            while (messageItr.hasNext()) {
                Message<String> message = messageItr.next();
                messageItr.remove();

                String trackName = message.getMessage();
                if(!trackName.equals(RESET_CODE)) {
                    MidiSequence sequence = midiSequenceManager.getResource(trackName).getData();
                    trackStartBroadcaster.setPushData(sequence);
                    trackStartBroadcaster.broadcast();
                }
                else{
                    sequencerResetBroadcaster.broadcast();
                }
            }
        }

        sliceBoard.ageAndCullMessages();
    }
}
