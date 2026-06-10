package util.datastructure;

import java.util.Arrays;

public final class ArrayUtil {
    private ArrayUtil(){}

    @SuppressWarnings("unchecked")
    public static <T> T[] addArrays(T[] arrayA, T[] arrayB){
        T[] toRet = (T[])Arrays.copyOf(arrayA, arrayA.length + arrayB.length, arrayA.getClass());
        System.arraycopy(arrayB, 0, toRet, arrayA.length, arrayB.length);
        return toRet;
    }

    @SafeVarargs
    public static <T> T[] addArrays(T[]... arrays){
        if(arrays.length == 0){
            return null;
        }
        if(arrays.length == 1){
            return arrays[0];
        }
        T[] total = addArrays(arrays[0], arrays[1]);
        if(arrays.length == 2){
            return total;
        }
        for(int i = 2; i < arrays.length; ++i){
            total = addArrays(total, arrays[i]);
        }
        return total;
    }
}
