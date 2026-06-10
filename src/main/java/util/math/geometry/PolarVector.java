package util.math.geometry;

public class PolarVector extends AbstractPolarVectorTemplate {
    public PolarVector() {
        super();
    }

    public PolarVector(double magnitude, Angle angle) {
        super(magnitude, angle);
    }

    public PolarVector(double magnitude, double angle) {
        super(magnitude, angle);
    }

    public PolarVector(AbstractVector toCopy) {
        super(toCopy);
    }

    private void updateAccordingToInnerCartesianVector(){
        magnitude = innerCartesianVector.getMagnitude();
        angle = innerCartesianVector.getAngle();
    }

    @Override
    public void setX(double x) {
        innerCartesianVector.setX(x);
        updateAccordingToInnerCartesianVector();
    }

    @Override
    public void setY(double y) {
        innerCartesianVector.setY(y);
        updateAccordingToInnerCartesianVector();
    }

    @Override
    public void addX(double x) {
        innerCartesianVector.addX(x);
        updateAccordingToInnerCartesianVector();
    }

    @Override
    public void addY(double y) {
        innerCartesianVector.addY(y);
        updateAccordingToInnerCartesianVector();
    }

    @Override
    public void scaleX(double scalar) {
        innerCartesianVector.scaleX(scalar);
        updateAccordingToInnerCartesianVector();
    }

    @Override
    public void scaleY(double scalar) {
        innerCartesianVector.scaleY(scalar);
        updateAccordingToInnerCartesianVector();
    }

    @Override
    public void setMagnitude(double newMagnitude) {
        magnitude = newMagnitude;
        updateInnerCartesianVector();
    }

    @Override
    public void addMagnitude(double magnitude) {
        this.magnitude += magnitude;
        updateInnerCartesianVector();
    }

    @Override
    public void scale(double scalar) {
        magnitude *= scalar;
        updateInnerCartesianVector();
    }

    @Override
    public void setAngle(Angle angle) {
        this.angle = angle;
        updateInnerCartesianVector();
    }

    @Override
    public void setAngle(double angle) {
        setAngle(new Angle(angle));
    }

    @Override
    public void addAngle(Angle angle) {
        this.angle = this.angle.add(angle);
    }

    @Override
    public void addAngle(double angle) {
        this.angle = this.angle.add(angle);
    }
}
