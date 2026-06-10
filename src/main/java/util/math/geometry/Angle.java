package util.math.geometry;

import util.math.interval.IntInterval;

import java.util.Objects;

@SuppressWarnings("unused")
public class Angle {

    private static final IntInterval ALLOWED_RANGE = IntInterval.makeLowerInclusiveOnly(0, 360);

    private final double angle;

    public Angle(){
        angle = 0;
    }

    public Angle(double angle) {
        if(ALLOWED_RANGE.isInInterval(angle)) {
            this.angle = angle;
        }
        else{
            this.angle = modIntoIntInterval(angle);
        }
    }

    private static double modIntoIntInterval(double d){
        double modD = d % ALLOWED_RANGE.size();
        if(modD < ALLOWED_RANGE.getLowerInclusive()){
            return modD + ALLOWED_RANGE.size();
        }
        return modD;
    }

    public double getAngle() {
        return angle;
    }

    public Angle add(double d){
        return new Angle(angle + d);
    }
    public Angle add(Angle other){
        return new Angle(angle + other.angle);
    }
    public Angle subtract(double d){
        return new Angle(angle - d);
    }
    public Angle subtract(Angle other){
        return new Angle(angle - other.angle);
    }
    public double smallerDifference(double d){
        double difference = angle - modIntoIntInterval(d);
        if(difference > 180){
            return -(360 - difference);
        }
        if(difference < -180){
            return 360 + difference;
        }
        return difference;
    }
    public double smallerDifference(Angle other){
        double difference = angle - other.angle;
        if(difference > 180){
            return -(360 - difference);
        }
        if(difference < -180){
            return 360 + difference;
        }
        return difference;
    }
    public double largerDifference(Angle other){
        double difference = angle - other.angle;
        if(difference < 180 && difference >= 0){
            return -(360 - difference);
        }
        if(difference > -180 && difference <= 0){
            return 360 + difference;
        }
        return difference;
    }

    public Angle flipY(){
        return new Angle(180 - angle);
    }
    public Angle flipX(){
        return new Angle(-angle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Angle)) return false;
        Angle angle1 = (Angle) o;
        return Double.compare(angle1.angle, angle) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(angle);
    }

    @Override
    public String toString() {
        return Double.toString(angle);
    }
}
