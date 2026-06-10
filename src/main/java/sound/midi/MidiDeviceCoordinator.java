package sound.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Sequencer;

interface MidiDeviceCoordinator {
    Sequencer getSequencer();
    MidiDevice getSynth();
    void cleanUp();
}
