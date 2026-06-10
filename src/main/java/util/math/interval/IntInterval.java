package util.math.interval;

import util.math.MathematicalException;

import java.util.Objects;

public class IntInterval implements Comparable<IntInterval>{
    private final int lowerInclusive;
    private final int higherExclusive;
    private final boolean isLowerInclusive;
    private final boolean isHigherInclusive;

    private IntInterval(int lower, int higher, boolean isLowerInclusive, boolean isHigherInclusive){
        this.lowerInclusive = lower;
        this.higherExclusive = higher;
        this.isLowerInclusive = isLowerInclusive;
        this.isHigherInclusive = isHigherInclusive;
    }

    public static IntInterval makeInclusive(int lower, int higher){
        if(lower > higher){
            throw new IllegalArgumentException("lower cannot be > higher!");
        }
        return new IntInterval(lower, higher + 1, true, true);
    }

    public static IntInterval makeExclusive(int lower, int higher){
        if(lower == higher){
            throw new IllegalArgumentException("Invalid interval for ExclusiveIntInterval" +
                    " (lower and higher cannot be the same");
        }
        if(lower > higher){
            throw new IllegalArgumentException("lower cannot be > higher!");
        }
        return new IntInterval(lower + 1, higher, false, false);
    }

    public static IntInterval makeLowerInclusiveOnly(int lower, int higher){
        if(lower > higher){
            throw new IllegalArgumentException("lower cannot be > higher!");
        }
        return new IntInterval(lower, higher, true, false);
    }

    public static IntInterval makeHigherInclusiveOnly(int lower, int higher){
        if(lower > higher){
            throw new IllegalArgumentException("lower cannot be > higher!");
        }
        return new IntInterval(lower + 1, higher + 1, false, true);
    }

    public static IntInterval makeZeroIntInterval(int i){
        return new IntInterval(i, i, true, false);
    }

    public IntInterval shift(int shift){
        return new IntInterval(lowerInclusive + shift, higherExclusive + shift,
                isLowerInclusive, isHigherInclusive);
    }

    public IntInterval shiftLower(int shift){
        int newLower = lowerInclusive + shift;
        if(newLower > higherExclusive){
            throw new MathematicalException("lower cannot exceed higher");
        }
        return new IntInterval(newLower, higherExclusive, isLowerInclusive, isHigherInclusive);
    }
    public IntInterval shiftHigher(int shift){
        int newHigher = higherExclusive + shift;
        if(newHigher < lowerInclusive){
            throw new MathematicalException("higher cannot be smaller than lower");
        }
        return new IntInterval(lowerInclusive, newHigher, isLowerInclusive, isHigherInclusive);
    }

    public IntInterval incrementLower(){
        return shiftLower(1);
    }
    public IntInterval decrementLower(){
        return shiftLower(-1);
    }
    public IntInterval incrementHigher(){
        return shiftHigher(1);
    }
    public IntInterval decrementHigher(){
        return shiftHigher(-1);
    }

    public int size(){
        return higherExclusive - lowerInclusive;
    }
    public boolean isEmpty(){
        return lowerInclusive == higherExclusive;
    }

    public int getLowerInclusive(){
        return lowerInclusive;
    }
    public int getLowerExclusive(){
        return lowerInclusive - 1;
    }
    public int getHigherInclusive(){
        return higherExclusive - 1;
    }
    public int getHigherExclusive(){
        return higherExclusive;
    }

    public boolean isInInterval(int i){
        return i >= lowerInclusive && i < higherExclusive;
    }

    public boolean isInInterval(double d){
        return d >= lowerInclusive && d < higherExclusive;
    }

    public int nearestIntervalElement(int i){
        if(isEmpty()){
            throw new MathematicalException("empty interval!");
        }
        if(isInInterval(i)){
            return i;
        }
        if(i < lowerInclusive){
            return lowerInclusive;
        }
        if(i > getHigherInclusive()){
            return getHigherInclusive();
        }
        throw new RuntimeException("unexpected line reached in IntInterval.makeIntoInterval()");
    }

    public int modIntoInterval(int i){
        if(isEmpty()){
            throw new MathematicalException("empty interval!");
        }
        if(isInInterval(i)){
            return i;
        }
        return i % size() + lowerInclusive;
    }

    public double modIntoInterval(double d){
        if(isEmpty()){
            throw new MathematicalException("empty interval!");
        }
        if(isInInterval(d)){
            return d;
        }
        return d % size() + lowerInclusive;
    }

    @Override
    public int compareTo(IntInterval other) {
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
        IntInterval intInterval = (IntInterval) o;
        return lowerInclusive == intInterval.lowerInclusive &&
                higherExclusive == intInterval.higherExclusive;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lowerInclusive, higherExclusive);
    }

    @Override
    public String toString() {
        return (isLowerInclusive ? "[" + lowerInclusive : "(" + (lowerInclusive - 1))
                + ", " + (isHigherInclusive ? (higherExclusive - 1) + "]" : higherExclusive + ")");
    }
}