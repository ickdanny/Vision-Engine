package util.math.geometry;

@SuppressWarnings("unused")
abstract class AbstractPolarVectorTemplate extends AbstractVectorTemplate {
    protected double magnitude;
    protected Angle angle;

    protected final CartesianVector innerCartesianVector;

    protected AbstractPolarVectorTemplate(){
        magnitude = 0;
        angle = new Angle();
        innerCartesianVector = new CartesianVector();
    }

    protected AbstractPolarVectorTemplate(double magnitude, Angle angle){
        this.magnitude = magnitude;
        this.angle = angle;
        innerCartesianVector = new CartesianVector();
        updateInnerCartesianVector();
    }

    protected AbstractPolarVectorTemplate(double magnitude, double angle){
        this(magnitude, new Angle(angle));
    }

    protected AbstractPolarVectorTemplate(AbstractVector toCopy){
        this(toCopy.getMagnitude(), toCopy.getAngle());
    }

    protected void updateInnerCartesianVector(){
        double angleInRadians = Math.toRadians(angle.getAngle());
        innerCartesianVector.setX(magnitude * Math.cos(angleInRadians));
        innerCartesianVector.setY(-1 * magnitude * Math.sin(angleInRadians));
    }

    @Override
    public double getX() {
        return innerCartesianVector.x;
    }

    @Override
    public double getY() {
        return innerCartesianVector.y;
    }

    @Override
    public double getMagnitude() {
        return magnitude;
    }

    @Override
    public Angle getAngle() {
        return angle;
    }
}
