package internalconfig.game.components;

import util.math.geometry.DoublePoint;

public class ButtonData {
    private boolean locked;
    private final DoublePoint unselPos;
    private final DoublePoint selPos;
    private final String baseImage;

    public ButtonData(DoublePoint pos, String baseImage){
        this(pos, pos, baseImage);
    }

    public ButtonData(DoublePoint unselPos, DoublePoint selPos, String baseImage) {
        this.locked = false;
        this.unselPos = unselPos;
        this.selPos = selPos;
        this.baseImage = baseImage;
    }

    public ButtonData(boolean locked, DoublePoint unselPos, DoublePoint selPos, String baseImage) {
        this.locked = locked;
        this.unselPos = unselPos;
        this.selPos = selPos;
        this.baseImage = baseImage;
    }

    public boolean isLocked() {
        return locked;
    }

    public DoublePoint getUnselPos() {
        return unselPos;
    }

    public DoublePoint getSelPos() {
        return selPos;
    }

    public String getSelImage(){
        return baseImage + "_sel";
    }

    public String getUnselImage(){
        return baseImage + "_unsel";
    }

    public String getLockedImage(){
        return baseImage + "_locked";
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
