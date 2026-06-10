package util.math.geometry;

@SuppressWarnings("unused")
public class CartesianVector extends AbstractCartesianVectorTemplate{
    public CartesianVector() {
        super();
    }

    public CartesianVector(double x, double y) {
        super(x, y);
    }

    public CartesianVector(AbstractVector toCopy) {
        super(toCopy);
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public void addX(double x){
        this.x += x;
    }

    @Override
    public void addY(double y){
        this.y += y;
    }

    @Override
    public void scaleX(double scalar) {
        x *= scalar;
    }

    @Override
    public void scaleY(double scalar) {
        y *= scalar;
    }

    @Override
    public void setMagnitude(double newMagnitude){
        double oldMagnitude = getMagnitude();
        double ratio = newMagnitude / oldMagnitude;
        x = x * ratio;
        y = y * ratio;
    }

    @Override
    public void addMagnitude(double magnitude) {
        setMagnitude(getMagnitude() + magnitude);
    }

    @Override
    public void scale(double scalar) {
        x *= scalar;
        y *= scalar;
    }

    @Override
    public void setAngle(double angle) {
        double magnitude = getMagnitude();
        double angleInRadians = Math.toRadians(angle);
        x = magnitude * Math.cos(angleInRadians);
        y = -1 * magnitude * Math.sin(angleInRadians);
    }

    @Override
    public void addAngle(double angle) {
        double newAngle = getAngle().getAngle() + angle;
        double magnitude = getMagnitude();
        double newAngleInRadians = Math.toRadians(newAngle);
        x = magnitude * Math.cos(newAngleInRadians);
        y = -1 * magnitude * Math.sin(newAngleInRadians);
    }


}
