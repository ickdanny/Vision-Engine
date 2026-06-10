package util.math.geometry;

import util.DeepCloneable;

import java.util.Objects;

@SuppressWarnings("unused")
public class AABB implements DeepCloneable<AABB> {

    private final double xLow;
    private final double xHigh;
    private final double yLow;
    private final double yHigh;

    public AABB(double radius){
        xLow = -radius;
        xHigh = radius;
        yLow = -radius;
        yHigh = radius;
    }

    public AABB(double x, double y){
        xLow = -x;
        xHigh = x;
        yLow = -y;
        yHigh = y;
    }

    public AABB(double xLow, double xHigh, double yLow, double yHigh) {
        this.xLow = xLow;
        this.xHigh = xHigh;
        this.yLow = yLow;
        this.yHigh = yHigh;
    }

    public double getXLow() {
        return xLow;
    }
    public double getXHigh() {
        return xHigh;
    }
    public double getYLow() {
        return yLow;
    }
    public double getYHigh() {
        return yHigh;
    }

    public double getWidth(){
        return Math.abs(xHigh - xLow);
    }
    public double getHeight(){
        return Math.abs(yHigh - yLow);
    }
    public double getArea(){
        return getWidth() * getHeight();
    }

    public DoublePoint getCenter(){
        return new DoublePoint((xLow + xHigh)/2, (yLow + yHigh)/2);
    }

    public AABB setXLow(double xLow) {
        return new AABB(xLow, xHigh, yLow, yHigh);
    }
    public AABB setXHigh(double xHigh) {
        return new AABB(xLow, xHigh, yLow, yHigh);
    }
    public AABB setYLow(double yLow) {
        return new AABB(xLow, xHigh, yLow, yHigh);
    }
    public AABB setYHigh(double yHigh) {
        return new AABB(xLow, xHigh, yLow, yHigh);
    }
    public AABB setX(double x){
        return new AABB(-x, x, yLow, yHigh);
    }
    public AABB setY(double y){
        return new AABB(xLow, xHigh, -y, y);
    }

    public AABB makeTrueHitbox(DoublePoint pos){
        return new AABB(xLow + pos.getX(),
                xHigh + pos.getX(),
                yLow + pos.getY(),
                yHigh + pos.getY());
    }

    public boolean collides(AABB other){
        return this.getXLow() <= other.getXHigh()
            && this.getXHigh() >= other.getXLow()
            && this.getYLow() <= other.getYHigh()
            && this.getYHigh() >= other.getYLow();
    }

    public static AABB makeTwoFrameHitbox(AABB a, AABB b){
        if(a == null){
            return b;
        }
        else if(b == null){
            return a;
        }
        return new AABB(Math.min(a.getXLow(), b.getXLow()), Math.max(a.getXHigh(), b.getXHigh()),
                Math.min(a.getYLow(), b.getYLow()), Math.max(a.getYHigh(), b.getYHigh()));
    }

    @Override
    public AABB deepClone() {
        return new AABB(xLow, xHigh, yLow, yHigh);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AABB)) return false;
        AABB aabb = (AABB) o;
        return Double.compare(aabb.xLow, xLow) == 0 &&
                Double.compare(aabb.xHigh, xHigh) == 0 &&
                Double.compare(aabb.yLow, yLow) == 0 &&
                Double.compare(aabb.yHigh, yHigh) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xLow, xHigh, yLow, yHigh);
    }
}
