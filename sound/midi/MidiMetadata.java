package sound.midi;

public class MidiMetadata {
    private static final long DEFAULT_END_POINT = -1;   //see Sequencer
    private static final long DEFAULT_START_POINT = 0;

    private final boolean looping;
    private final long loopStartPoint;
    private final long loopEndPoint;

    public static final MidiMetadata DEFAULT_LOOPING = new MidiMetadata(true);
    public static final MidiMetadata DEFAULT_NOT_LOOPING = new MidiMetadata(false);

    public MidiMetadata(boolean looping){
        this.looping = looping;
        loopStartPoint = DEFAULT_START_POINT;
        loopEndPoint = DEFAULT_END_POINT;
    }

    public MidiMetadata(boolean looping, long loopStartPoint){
        this.looping = looping;
        this.loopStartPoint = loopStartPoint;
        loopEndPoint = DEFAULT_END_POINT;
    }
    public MidiMetadata(boolean looping, long loopStartPoint, long loopEndPoint) {
        this.looping = looping;
        this.loopStartPoint = loopStartPoint;
        this.loopEndPoint = loopEndPoint;
    }

    public boolean isLooping() {
        return looping;
    }
    public long getLoopStartPoint() {
        return loopStartPoint;
    }
    public long getLoopEndPoint() {
        return loopEndPoint;
    }
}