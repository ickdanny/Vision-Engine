package internalconfig.game.components;

import internalconfig.game.GameConfig;
import util.Ticker;

public class AnimationComponent {
    private final Animation[] animations;
    private final int idleIndex;
    private final Ticker ticker;

    private int currentIndex;

    public AnimationComponent(Animation[] animations, int idleIndex){
        this.animations = animations;
        this.idleIndex = idleIndex;
        ticker = new Ticker(GameConfig.ANIMATION_TICK, true);
        currentIndex = idleIndex;
    }

    public AnimationComponent(Animation[] animations, int idleIndex, int tick){
        this.animations = animations;
        this.idleIndex = idleIndex;
        ticker = new Ticker(tick, true);
        currentIndex = idleIndex;
    }

    @SuppressWarnings("ConstantConditions")
    public AnimationComponent(Animation animation){
        animations = new Animation[]{animation};
        idleIndex = 0;
        ticker = new Ticker(GameConfig.ANIMATION_TICK, true);
        currentIndex = idleIndex;
    }

    @SuppressWarnings("ConstantConditions")
    public AnimationComponent(Animation animation, int tick){
        animations = new Animation[]{animation};
        idleIndex = 0;
        ticker = new Ticker(tick, true);
        currentIndex = idleIndex;
    }

    public Animation[] getAnimations() {
        return animations;
    }

    public Animation getCurrentAnimation(){
        return animations[currentIndex];
    }

    public int getIdleIndex() {
        return idleIndex;
    }

    public Ticker getTicker() {
        return ticker;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }
}