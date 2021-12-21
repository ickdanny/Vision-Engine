package internalconfig.game.components;

public class Animation {
    private final String[] frames;
    private final boolean looping;
    private int currentIndex;

    public Animation(boolean looping, String... frames) {
        if(frames.length < 1){
            throw new IllegalArgumentException("too few frames!");
        }
        this.frames = frames;
        this.looping = looping;
        currentIndex = 0;
    }

    public String[] getFrames() {
        return frames;
    }

    public String getCurrentFrame(){
        return frames[currentIndex];
    }

    public boolean isLooping() {
        return looping;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }
}