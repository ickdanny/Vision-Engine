package util.math.geometry;

import util.ConstException;

@SuppressWarnings("unused")
public class ConstPolarVector extends AbstractPolarVectorTemplate {
    public ConstPolarVector() {
        super();
    }

    public ConstPolarVector(double magnitude, Angle angle) {
        super(magnitude, angle);
    }

    public ConstPolarVector(double magnitude, double angle) {
        super(magnitude, angle);
    }

    public ConstPolarVector(AbstractVector toCopy) {
        super(toCopy);
    }

    @Override
    public void setX(double x) {
        throw makeException("setX()");
    }

    @Override
    public void setY(double y) {
        throw makeException("setY()");
    }

    @Override
    public void addX(double x) {
        throw makeException("addX()");
    }

    @Override
    public void addY(double y) {
        throw makeException("addY()");
    }

    @Override
    public void scaleX(double scalar) {
        throw makeException("scaleX()");
    }

    @Override
    public void scaleY(double scalar) {
        throw makeException("scaleY()");
    }

    @Override
    public void setMagnitude(double newMagnitude) {
        throw makeException("setMagnitude()");
    }

    @Override
    public void addMagnitude(double magnitude) {
        throw makeException("addMagnitude()");
    }

    @Override
    public void scale(double scalar) {
        throw makeException("scale()");
    }

    @Override
    public void setAngle(double angle) {
        throw makeException("setAngle()");
    }

    @Override
    public void addAngle(double angle) {
        throw makeException("addAngle()");
    }

    private RuntimeException makeException(String methodName){
        return new ConstException("Cannot use " + methodName + " for ConstCartesianVector");
    }
}
