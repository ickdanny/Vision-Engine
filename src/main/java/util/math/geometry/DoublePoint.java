package util.math.geometry;

import util.DeepCloneable;

import java.util.Objects;

@SuppressWarnings("unused")
public class DoublePoint implements DeepCloneable<DoublePoint> {
    private double x;
    private double y;

    public DoublePoint(){
        x = 0;
        y = 0;
    }

    public DoublePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public DoublePoint(DoublePoint toCopy){
        x = toCopy.x;
        y = toCopy.y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setAs(DoublePoint other){
        this.x = other.x;
        this.y = other.y;
    }

    public boolean isZero(){
        return x == 0 && y == 0;
    }

    @Override
    public DoublePoint deepClone(){
        return new DoublePoint(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoublePoint)) return false;
        DoublePoint that = (DoublePoint) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
