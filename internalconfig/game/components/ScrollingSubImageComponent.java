package internalconfig.game.components;

public class ScrollingSubImageComponent {
    private double x;
    private double y;
    private double width;
    private double height;

    private double xVelocity;
    private double yVelocity;
    private double widthVelocity;
    private double heightVelocity;

    public ScrollingSubImageComponent(double x, double y, double width, double height, double xVelocity, double yVelocity, double widthVelocity, double heightVelocity) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
        this.widthVelocity = widthVelocity;
        this.heightVelocity = heightVelocity;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getXVelocity() {
        return xVelocity;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void setxVelocity(double xVelocity) {
        this.xVelocity = xVelocity;
    }

    public double getYVelocity() {
        return yVelocity;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void setyVelocity(double yVelocity) {
        this.yVelocity = yVelocity;
    }

    public double getWidthVelocity() {
        return widthVelocity;
    }

    public void setWidthVelocity(double widthVelocity) {
        this.widthVelocity = widthVelocity;
    }

    public double getHeightVelocity() {
        return heightVelocity;
    }

    public void setHeightVelocity(double heightVelocity) {
        this.heightVelocity = heightVelocity;
    }
}
