package util.math.interval;

import util.math.MathematicalException;

import java.util.Objects;

@SuppressWarnings("unused")
public class DoubleInterval implements Comparable<DoubleInterval>{
    private final double lowerInclusive;
    private final double higherExclusive;
    private final boolean isLowerInclusive;
    private final boolean isHigherInclusive;

    private DoubleInterval(double lower, double higher, boolean isLowerInclusive, boolean isHigherInclusive){
        this.lowerInclusive = lower;
        this.higherExclusive = higher;
        this.isLowerInclusive = isLowerInclusive;
        this.isHigherInclusive = isHigherInclusive;
    }

    public static DoubleInterval makeInclusive(double lower, double higher){
        if(lower > higher){
            throw new IllegalArgumentException("lower cannot be > higher!");
        }
        return new DoubleInterval(lower, Math.nextUp(higher), true, true);
    }

    public static DoubleInterval makeExclusive(double lower, double higher){
        if(lower == higher){
            throw new IllegalArgumentException("Invalid interval for ExclusiveDoubleInterval" +
                    " (lower and higher cannot be the same");
        }
        if(lower > higher){
            throw new IllegalArgumentException("lower cannot be > higher!");
        }
        return new DoubleInterval(Math.nextUp(lower), higher, false, false);
    }

    public static DoubleInterval makeLowerInclusiveOnly(double lower, double higher){
        if(lower > higher){
            throw new IllegalArgumentException("lower cannot be > higher!");
        }
        return new DoubleInterval(lower, higher, true, false);
    }

    public static DoubleInterval makeHigherInclusiveOnly(double lower, double higher){
        if(lower > higher){
            throw new IllegalArgumentException("lower cannot be > higher!");
        }
        return new DoubleInterval(Math.nextUp(lower), Math.nextUp(higher), false, true);
    }

    public static DoubleInterval makeZeroDoubleInterval(double d){
        return new DoubleInterval(d, d, true, false);
    }

    public DoubleInterval shift(double shift){
        return new DoubleInterval(lowerInclusive + shift, higherExclusive + shift,
                isLowerInclusive, isHigherInclusive);
    }

    public DoubleInterval shiftLower(double shift){
        double newLower = lowerInclusive + shift;
        if(newLower > higherExclusive){
            throw new MathematicalException("lower cannot exceed higher");
        }
        return new DoubleInterval(newLower, higherExclusive, isLowerInclusive, isHigherInclusive);
    }
    public DoubleInterval shiftHigher(double shift){
        double newHigher = higherExclusive + shift;
        if(newHigher < lowerInclusive){
            throw new MathematicalException("higher cannot be smaller than lower");
        }
        return new DoubleInterval(lowerInclusive, newHigher, isLowerInclusive, isHigherInclusive);
    }

    public double size(){
        return higherExclusive - lowerInclusive;
    }
    public boolean isEmpty(){
        return lowerInclusive == higherExclusive;
    }

    public double getLowerInclusive(){
        return lowerInclusive;
    }
    public double getLowerExclusive(){
        return Math.nextDown(lowerInclusive);
    }
    public double getHigherInclusive(){
        return Math.nextDown(higherExclusive);
    }
    public double getHigherExclusive(){
        return higherExclusive;
    }

    public boolean isInInterval(int i){
        return i >= lowerInclusive && i < higherExclusive;
    }

    public boolean isInInterval(double d){
        return d >= lowerInclusive && d < higherExclusive;
    }

    public double makeIntoInterval(double d){
        if(isEmpty()){
            throw new MathematicalException("empty interval!");
        }
        if(isInInterval(d)){
            return d;
        }
        if(d < lowerInclusive){
            return lowerInclusive;
        }
        if(d > getHigherInclusive()){
            return getHigherExclusive();
        }
        throw new RuntimeException("unexpected line reached in DoubleInterval.makeIntoInterval()");
    }

    @Override
    public int compareTo(DoubleInterval other) {
        if(this.lowerInclusive < other.lowerInclusive){
            return -1;
        }
        else if(this.lowerInclusive > other.lowerInclusive){
            return 1;
        }
        if(this.higherExclusive < other.higherExclusive){
            return -1;
        }
        else if(this.higherExclusive > other.higherExclusive){
            return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleInterval doubleInterval = (DoubleInterval) o;
        return lowerInclusive == doubleInterval.lowerInclusive &&
                higherExclusive == doubleInterval.higherExclusive;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lowerInclusive, higherExclusive);
    }

    @Override
    public String toString() {
        return (isLowerInclusive ? "[" + lowerInclusive : "(" + Math.nextDown(lowerInclusive))
                + ", " + (isHigherInclusive ? Math.nextDown(higherExclusive) + "]" : higherExclusive + ")");
    }
}