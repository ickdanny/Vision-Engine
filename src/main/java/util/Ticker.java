package util;

public class Ticker {
    protected int tick;
    protected final int maxTick;
    protected final boolean loop;

    public Ticker(int maxTick, boolean loop) {
        tick = maxTick;
        this.maxTick = maxTick;
        this.loop = loop;
    }

    //return <= 0 if over
    public int stepAndGetTick(){
        int toRet = tick--;
        if(loop){
            if(tick <= 0){
                tick = maxTick;
            }
        }
        return toRet;
    }
}
