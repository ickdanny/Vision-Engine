package sound.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;

@SuppressWarnings("unused")
class DefaultMidiDeviceCoordinator implements MidiDeviceCoordinator{
    private static final String DEFAULT_SYNTH_NAME = "Microsoft MIDI Mapper";

    private static Sequencer sequencer;
    private static MidiDevice synth;

    public DefaultMidiDeviceCoordinator(){
        init();
    }

    @Override
    public Sequencer getSequencer() {
        if(sequencer == null){
            init();
        }
        return sequencer;
    }
    @Override
    public MidiDevice getSynth() {
        if(synth == null){
            init();
        }
        return synth;
    }

    private static void init(){
        sequencer = getMidiSystemSequencer();
        clearSequencer(sequencer);

        synth = getMidiSystemSynth();

        Receiver synthReceiver = getReceiverOfSynth(synth);

        Transmitter sequencerTransmitter = getTransmitterOfSequencer(sequencer);
        clearTransmitter(sequencerTransmitter);

        sequencerTransmitter.setReceiver(synthReceiver);

        openSynth(synth);
        openSequencer(sequencer);
    }

    private static Sequencer getMidiSystemSequencer(){
        try {
            return MidiSystem.getSequencer();
        }catch(MidiUnavailableException mue){
            throw new RuntimeException("Unable to get sequencer", mue);
        }
    }
    private static void clearSequencer(Sequencer sequencer){
        for(Receiver r : sequencer.getReceivers()){
            r.close();
        }
        for(Transmitter t : sequencer.getTransmitters()){
            t.close();
        }
    }
    private static MidiDevice getMidiSystemSynth(){
        try {
            for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
                String name = info.getName();
                if (name.equals(DEFAULT_SYNTH_NAME)) {
                    return MidiSystem.getMidiDevice(info);
                }
            }
        }catch(MidiUnavailableException mue){
            throw new RuntimeException("Unable to open MIDI device \"" +  DEFAULT_SYNTH_NAME + '"', mue);
        }
        throw new RuntimeException("Unable to retrieve default synthesizer \"" + DEFAULT_SYNTH_NAME + '"');
    }
    private static MidiDevice getMidiSystemSynth(String synthName){
        MidiDevice.Info defaultSynthInfo = null;
        try {
            for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
                String name = info.getName();
                if (name.equals(synthName)) {
                    return MidiSystem.getMidiDevice(info);
                }
                if (name.equals(DEFAULT_SYNTH_NAME)) {
                    defaultSynthInfo = info;
                }
            }
            if (defaultSynthInfo != null) {
                return MidiSystem.getMidiDevice(defaultSynthInfo);
            }
        }catch(MidiUnavailableException mue){
            throw new RuntimeException("Unable to open MIDI device \"" + synthName + '"', mue);
        }
        throw new RuntimeException("Unable to retrieve any Synthesizer");
    }
    private static Receiver getReceiverOfSynth(MidiDevice synth){
        try{
            return synth.getReceiver();
        }catch(MidiUnavailableException mue){
            throw new RuntimeException("Unable to get receiver of synth", mue);
        }
    }
    private static Transmitter getTransmitterOfSequencer(Sequencer sequencer){
        try{
            return sequencer.getTransmitter();
        }catch(MidiUnavailableException mue){
            throw new RuntimeException("Unable to get transmitter of sequencer", mue);
        }
    }
    private static void clearTransmitter(Transmitter transmitter){
        if(transmitter.getReceiver() != null) {
            transmitter.getReceiver().close();
        }
    }
    private static void openSynth(MidiDevice synth){
        try{
            synth.open();
        }catch(MidiUnavailableException mue){
            throw new RuntimeException("Unable to open synth", mue);
        }
    }
    private static void openSequencer(Sequencer sequencer){
        try{
            sequencer.open();
        }catch(MidiUnavailableException mue){
            throw new RuntimeException("Unable to open sequencer", mue);
        }
    }

    @Override
    public void cleanUp(){
        synth.close();
        sequencer.stop();
        sequencer.close();
    }
}
