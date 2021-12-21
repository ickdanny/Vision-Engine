package internalconfig.game.components;

public enum DrawPlane{
    BACKGROUND(DrawFrom.CORNER),
    MIDGROUND(DrawFrom.MIDDLE),
    FOREGROUND(DrawFrom.CORNER)
    ;
    private final DrawFrom drawFrom;

    DrawPlane(DrawFrom drawFrom){
        this.drawFrom = drawFrom;
    }

    public DrawFrom getDrawFrom() {
        return drawFrom;
    }

    public enum DrawFrom{
        CORNER,
        MIDDLE
    }
}
