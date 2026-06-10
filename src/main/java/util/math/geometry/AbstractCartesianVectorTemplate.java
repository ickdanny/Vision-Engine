package util.math.geometry;

@SuppressWarnings("unused")
abstract class AbstractCartesianVectorTemplate extends AbstractVectorTemplate{
    protected double x;
    protected double y;

    protected AbstractCartesianVectorTemplate(){
        x = 0;
        y = 0;
    }

    protected AbstractCartesianVectorTemplate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    protected AbstractCartesianVectorTemplate(AbstractVector toCopy){
        this(toCopy.getX(), toCopy.getY());
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getMagnitude() {
        if(x == 0){
            return Math.abs(y);
        }
        if(y == 0){
            return Math.abs(x);
        }
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    @Override
    public Angle getAngle() {
        return new Angle(-Math.toDegrees(Math.atan2(x, -y)) + 90);
    }
}
