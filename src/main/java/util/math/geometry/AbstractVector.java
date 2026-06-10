package util.math.geometry;

@SuppressWarnings("unused")
public interface AbstractVector {
    double getX();
    double getY();
    double getMagnitude();
    Angle getAngle();

    void setX(double x);
    void setY(double y);
    void addX(double x);
    void addY(double y);
    void scaleX(double scalar);
    void scaleY(double scalar);

    void setMagnitude(double newMagnitude);
    void addMagnitude(double magnitude);
    void scale(double scalar);
    void setAngle(double angle);
    default void setAngle(Angle angle){
        setAngle(angle.getAngle());
    }
    void addAngle(double angle);
    default void addAngle(Angle angle){
        addAngle(angle.getAngle());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean isZero(){
        return getMagnitude() == 0;
    }

    default DoublePoint lerp(DoublePoint point, double d){
        double dx = d * getX();
        double dy = d * getY();
        return new DoublePoint(point.getX() + dx, point.getY() + dy);
    }
    default DoublePoint add(DoublePoint point){
        return new DoublePoint(point.getX() + this.getX(), point.getY() + this.getY());
    }
    default DoublePoint multiAdd(double multiplier, DoublePoint point){
        return new DoublePoint(point.getX() + (multiplier * this.getX()), point.getY() + (multiplier * this.getY()));
    }
}
