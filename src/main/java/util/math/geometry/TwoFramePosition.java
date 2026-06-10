package util.math.geometry;

public class TwoFramePosition {
    private final DoublePoint pastPos;
    private final DoublePoint pos;

    public TwoFramePosition() {
        this(new DoublePoint());
    }

    public TwoFramePosition(DoublePoint pos) {
        pastPos = pos.deepClone();
        this.pos = pos.deepClone();
    }

    public void step(){
        pastPos.setAs(pos);
    }

    public DoublePoint getPastPos() {
        return pastPos;
    }

    public DoublePoint getPos() {
        return pos;
    }
}
