package sound.midi;

import util.observer.AbstractObserver;
import util.observer.AbstractPushObserver;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequencer;
import java.io.IOException;

@SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
public class MusicController {

    private final MidiDeviceCoordinator midiDeviceCoordinator;
    private final AbstractPushObserver<MidiSequence> trackStartReceiver;
    private final AbstractObserver sequencerResetReceiver;
    private final AbstractObserver muteToggleReceiver;

    private boolean muted;

    private MusicController(MidiDeviceCoordinator midiDeviceCoordinator, boolean muted){
        this.midiDeviceCoordinator = midiDeviceCoordinator;
        trackStartReceiver = makeTrackStartReceiver();
        sequencerResetReceiver = makeSequencerResetReceiver();
        muteToggleReceiver = makeMuteToggleReceiver();
        this.muted = muted;
    }

    public static MusicController makeDefaultMusicController(boolean muted){
        return new MusicController(new DefaultMidiDeviceCoordinator(), muted);
    }

    private void startTrack(MidiSequence sequence){
        if(!muted) {
            Sequencer sequencer = midiDeviceCoordinator.getSequencer();
            resetSequencer(sequencer);
            sequence.reset();
            try {
                sequencer.setSequence(sequence.getMidiInputStream());
                initSequenceMetadata(sequencer, sequence.getMetadata());
                sequencer.start();
            } catch (InvalidMidiDataException | IOException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void resetSequencer(Sequencer sequencer){
        sequencer.stop();
    }
    private void initSequenceMetadata(Sequencer sequencer, MidiMetadata metadata){
        if(metadata.isLooping()){
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.setLoopEndPoint(metadata.getLoopEndPoint());
            sequencer.setLoopStartPoint(metadata.getLoopStartPoint());
        }else{
            sequencer.setLoopCount(0);
        }
    }
    private void toggleMute(){
        if(!muted){
            resetSequencer(midiDeviceCoordinator.getSequencer());
            muted = true;
        }
        else{
            muted = false;
        }
    }

    private AbstractPushObserver<MidiSequence> makeTrackStartReceiver(){
        return new AbstractPushObserver<MidiSequence>() {
            @Override
            public void update(MidiSequence sequence) {
                startTrack(sequence);
            }
        };
    }
    private AbstractObserver makeSequencerResetReceiver(){
        return new AbstractObserver() {
            @Override
            public void update() {
                resetSequencer(midiDeviceCoordinator.getSequencer());
            }
        };
    }
    private AbstractObserver makeMuteToggleReceiver(){
        return new AbstractObserver() {
            @Override
            public void update() {
                toggleMute();
            }
        };
    }

    public AbstractPushObserver<MidiSequence> getTrackStartReceiver(){
        return trackStartReceiver;
    }
    public AbstractObserver getSequencerResetReceiver(){
        return sequencerResetReceiver;
    }
    public AbstractObserver getMuteToggleReceiver() {
        return muteToggleReceiver;
    }

    public void cleanUp(){
        midiDeviceCoordinator.cleanUp();
    }
}