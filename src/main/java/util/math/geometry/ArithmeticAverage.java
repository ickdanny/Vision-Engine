package util.math.geometry;

public class ArithmeticAverage {
    private double total;
    private int entries;
    private double average;

    public ArithmeticAverage(){
        total = 0;
        entries = 0;
        average = 0;
    }

    public double addEntry(double entry){
        total += entry;
        ++entries;
        return calculateAverage();
    }

    public double getAverage(){
        return average;
    }

    private double calculateAverage(){
        return (average = total / entries);
    }
}
