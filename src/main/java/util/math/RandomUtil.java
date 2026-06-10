package util.math;

import java.util.Random;

public final class RandomUtil {
    private RandomUtil(){}

    public static int randIntInclusive(int low, int high, Random random){
        return random.nextInt(high - low + 1) + low;
    }

    public static double randDoubleInclusive(double low, double high, Random random){
        return random.doubles(1, low, Math.nextAfter(high, Double.POSITIVE_INFINITY)).iterator().next();
    }
}
