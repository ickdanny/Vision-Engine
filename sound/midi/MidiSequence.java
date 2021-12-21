package sound.midi;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class MidiSequence {
    private final BufferedInputStream midiInputStream;
    private final MidiMetadata metadata;

    public MidiSequence(InputStream inputStream, MidiMetadata metadata) {
        midiInputStream = new BufferedInputStream(inputStream);
        midiInputStream.mark(Integer.MAX_VALUE);
        this.metadata = metadata;
    }

    public void reset(){
        try {
            midiInputStream.reset();
        }catch(IOException ioe){
            throw new UncheckedIOException("Unable to reset inputStream", ioe);
        }
    }

    public void close(){
        try {
            midiInputStream.close(); //will close the underlying stream
        }catch(IOException ioe){
            //fail silently
        }
    }

    public BufferedInputStream getMidiInputStream() {
        return midiInputStream;
    }
    public MidiMetadata getMetadata() {
        return metadata;
    }
}
